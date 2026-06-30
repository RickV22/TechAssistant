# TechAssistant 

¡Hola! Este es mi proyecto de estudios donde estoy aprendiendo a desarrollar apps para Android. Es una aplicación de soporte técnico que usa inteligencia artificial para ayudar a resolver problemas de computadoras.

La idea es que puedas escribir qué le pasa a tu PC (como "mi pantalla se ve azul" o "no tengo internet") y un técnico virtual te ayuda paso a paso.

## Lo que estoy aprendiendo con esta App

- **Kotlin y Jetpack Compose**: Estoy practicando cómo hacer interfaces modernas y bonitas.
- **IA con Gemini**: Aprendí a conectar la API de Google (Gemini 2.5) para que la app pueda "pensar" y responder.
- **Bases de Datos (Room)**: Al principio me costó mucho configurar esto (¡tuve problemas con KSP!), pero logré que la app guarde el historial de los chats para que no se borren.
- **Arquitectura**: Estoy intentando separar la lógica de la pantalla usando ViewModels.

## que hace la app

- Te responde como si fuera un técnico experto pero buena onda.
- Guarda tus conversaciones para que puedas verlas después.
- Tiene un diseño oscuro (Dark Mode) porque me gusta cómo se ve.
- ¡mantiene el hilo de la conversación y no se olvida de lo que habla!

##

1. Clona el proyecto.
2. **IMPORTANTE**: Tienes que poner tu propio archivo `google-services.json` de Firebase en la carpeta `/app`, porque el mío no lo subí por seguridad (¡GitHub me mandó un aviso de secretos!).
3. Abre el proyecto en Android Studio y dale a Play.

## ⚠️ Nota sobre los "Secretos" de GitHub
Si lo vas a subir a tu propio repo, ten cuidado con las claves de la API. GitHub detecta si dejas claves privadas a la vista. ¡Asegúrate de configurar bien el `.gitignore`!

