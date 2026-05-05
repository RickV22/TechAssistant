package com.example.techassistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techassistant.data.AppDatabase
import com.example.techassistant.data.ChatMessage
import com.example.techassistant.data.ChatSession
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TechSupportViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao = AppDatabase.getDatabase(application).chatDao()
    
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    // Usar flatMapLatest para que el flujo de mensajes cambie automáticamente al cambiar el ID de sesión
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentMessages: StateFlow<List<ChatMessage>> = _currentSessionId
        .flatMapLatest { sessionId ->
            if (sessionId == null) flowOf(emptyList())
            else chatDao.getMessagesForSession(sessionId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-2.5-flash-lite",
    )

    init {
        viewModelScope.launch {
            chatDao.getAllSessions().collect {
                _sessions.value = it
            }
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
        _uiState.value = UiState.Initial
    }

    fun createNewChat() {
        _currentSessionId.value = null
        _uiState.value = UiState.Initial
    }

    fun diagnoseProblem(problem: String) {
        if (problem.isBlank()) {
            _uiState.value = UiState.Error("El campo no puede estar vacío")
            return
        }

        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var sessionId = _currentSessionId.value
                
                if (sessionId == null) {
                    val title = if (problem.length > 30) problem.take(27) + "..." else problem
                    sessionId = chatDao.insertSession(ChatSession(title = title))
                    _currentSessionId.value = sessionId
                }

                // Guardar mensaje del usuario en la BD
                chatDao.insertMessage(ChatMessage(sessionId = sessionId!!, content = problem, isFromUser = true))

                // Convertir el historial actual al formato de Gemini
                val chatHistory = currentMessages.value.map { msg ->
                    content(role = if (msg.isFromUser) "user" else "model") {
                        text(msg.content)
                    }
                }

                val systemInstruction = "Eres un técnico amigo y experto. Responde de forma breve, natural y directa. " +
                        "Evita listas excesivamente largas o un tono demasiado robótico. " +
                        "Ve al grano pero mantén un trato amable y humano. Si la solución es simple, dila en un par de frases. " +
                        "Prioriza la solución más probable primero."

                // Iniciar chat con el historial acumulado
                val chat = generativeModel.startChat(history = chatHistory)
                
                // Enviar el nuevo mensaje
                val response = chat.sendMessage("$systemInstruction\n\nProblema: $problem")
                val outputContent = response.text
                
                if (outputContent != null) {
                    // Guardar respuesta de la IA en la BD
                    chatDao.insertMessage(ChatMessage(sessionId = sessionId!!, content = outputContent, isFromUser = false))
                    _uiState.value = UiState.Success(outputContent)
                } else {
                    _uiState.value = UiState.Error("Respuesta vacía de la IA")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            chatDao.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                createNewChat()
            }
        }
    }
}