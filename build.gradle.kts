// Configuración general de plugins para los módulos del proyecto.
plugins {
    // Plugin para compilar aplicaciones Android.
    alias(libs.plugins.android.application) apply false

    // Plugin para integrar Kotlin con Jetpack Compose.
    alias(libs.plugins.kotlin.compose) apply false

    // Declara KSP para que los módulos puedan utilizarlo.
    alias(libs.plugins.ksp) apply false
}