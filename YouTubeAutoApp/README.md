# YouTube Auto App - Proyecto Universitario

## Descripción
Esta aplicación es un **proyecto de concepto** para demostrar la integración de YouTube con Android Auto. Está inspirada en el proyecto [Fermata Auto](https://github.com/AndreyPavlenko/Fermata).

## ⚠️ ADVERTENCIA IMPORTANTE
- Esta aplicación está diseñada **ÚNICAMENTE para fines educativos y de demostración**.
- **NO** debe usarse mientras se conduce un vehículo.
- Android Auto bloquea la reproducción de video mientras el vehículo está en movimiento por razones de seguridad.
- La funcionalidad completa solo está disponible cuando el vehículo está detenido o en un simulador.

## Características
- Integración con Android Auto usando `androidx.car.app`
- Interfaz de usuario optimizada para pantallas de automóvil
- Reproductor de YouTube integrado
- Lista de videos sugeridos
- Soporte para pantalla completa en modo proyección

## Requisitos Técnicos
- Android SDK 26+ (Android 8.0)
- Android Studio Arctic Fox o superior
- Gradle 8.0+
- Kotlin 1.9+

## Estructura del Proyecto
```
YouTubeAutoApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/youtubeauto/
│   │   │   ├── MainActivity.kt
│   │   │   ├── VideoProjectionActivity.kt
│   │   │   ├── YouTubeCarAppService.kt
│   │   │   ├── model/
│   │   │   │   └── YouTubeVideo.kt
│   │   │   ├── player/
│   │   │   │   └── SurfaceRenderer.kt
│   │   │   └── screens/
│   │   │       ├── YouTubeHomeScreen.kt
│   │   │       └── YouTubeVideoDetailScreen.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   ├── drawable/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## Cómo Compilar
1. Abre el proyecto en Android Studio
2. Configura la ruta del SDK en `local.properties`
3. Sincroniza el proyecto con Gradle
4. Compila el APK desde Build > Build Bundle(s) / APK(s) > Build APK(s)

## Cómo Probar
### Opción 1: Simulador de Android Auto (Desktop Head Unit)
1. Descarga el [Desktop Head Unit](https://developer.android.com/training/cars/testing#desktop-head-unit)
2. Ejecuta la aplicación en un emulador de Android
3. Conecta el emulador al DHU
4. Prueba la interfaz de Android Auto

### Opción 2: Dispositivo Real (Solo cuando el vehículo esté detenido)
1. Instala el APK en tu dispositivo Android
2. Conecta el dispositivo a un sistema Android Auto compatible
3. La aplicación aparecerá en la lista de aplicaciones de Android Auto

## Limitaciones Técnicas
- **Reproducción de video bloqueada en movimiento**: Android Auto detecta la velocidad del vehículo y bloquea contenido de video por seguridad.
- **API oficial de YouTube**: La aplicación usa la biblioteca youtube-player, que requiere conexión a internet.
- **Permisos especiales**: Algunos sistemas de coche pueden requerir configuración adicional.

## Tecnologías Utilizadas
- **Kotlin**: Lenguaje de programación principal
- **AndroidX Car App Library**: Biblioteca oficial para desarrollo de Android Auto
- **youtube-player**: Biblioteca de terceros para reproducción de YouTube
- **Material Design**: Componentes de UI

## Referencias
- [Documentación oficial de Android Auto](https://developer.android.com/training/cars)
- [Fermata Auto (Inspiración)](https://github.com/AndreyPavlenko/Fermata)
- [YouTube Player Library](https://github.com/PierfrancescoSironi/youtube-android-player)

## Licencia
Este proyecto es solo para fines educativos. No tiene licencia comercial.

## Descargo de Responsabilidad
Esta aplicación no está afiliada con Google, YouTube o Android Auto. El uso de esta aplicación mientras se conduce puede ser ilegal en tu jurisdicción. El autor no se responsabiliza por ningún mal uso de esta aplicación.
