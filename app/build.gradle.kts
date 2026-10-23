plugins {
    // Plugin principal de Android.
    alias(libs.plugins.android.application)

    // Integración de Kotlin con Jetpack Compose.
    alias(libs.plugins.kotlin.compose)

    // Procesador de anotaciones para Room.
    alias(libs.plugins.ksp)
}

android {
    // Identificador del paquete de la aplicación.
    namespace = "com.mastermartini.app"

    // Versión del SDK con el que se compila.
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        // Identificador único de la aplicación.
        applicationId = "com.mastermartini.app"

        // Versión mínima y objetivo de Android.
        minSdk = 24
        targetSdk = 37

        // Versión inicial de la aplicación.
        versionCode = 1
        versionName = "1.0"

        // Ejecutor de las pruebas instrumentadas.
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Configuración de la compilación de lanzamiento.
        release {
            optimization {
                enable = false
            }
        }
    }

    // Compatibilidad del código con Java 11.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Habilita Jetpack Compose.
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Funciones principales de Android.
    implementation(libs.androidx.core.ktx)

    // Componentes de interfaz de Jetpack Compose.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Ciclo de vida y ViewModel.
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navegación entre pantallas.
    implementation(libs.androidx.navigation.compose)

    // Base de datos local Room.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Genera automáticamente el código de las entidades y los DAO.
    ksp(libs.androidx.room.compiler)

    // Pruebas unitarias.
    testImplementation(libs.junit)

    // Pruebas instrumentadas de la aplicación.
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Herramientas de depuración de Compose.
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}