// Plugins aplicados al módulo de la aplicación.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // Plugin KSP necesario para generar código de Room
}

// Configuración general del proyecto Android.
android {
    namespace = "com.mastermartini.app"

    compileSdk = 37

    // Configuración predeterminada de la aplicación.
    defaultConfig {
        applicationId = "com.mastermartini.app"
        minSdk = 24
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Tipos de compilación (Release y Debug).
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Opciones de compatibilidad para Java 11.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Habilita el soporte para Jetpack Compose.
    buildFeatures {
        compose = true
    }
}

// Dependencias y librerías del módulo.
dependencies {

    // Componentes base del sistema Android.
    implementation(libs.androidx.core.ktx)

    // Componentes de Jetpack Compose y Material 3.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Gestión del ciclo de vida y ViewModels en Compose.
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Navegación entre pantallas en Compose.
    implementation(libs.androidx.navigation.compose)

    // Base de datos local Room y su compilador KSP.
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // Genera AppDatabase_Impl para solucionar el crash al abrir

    // Pruebas unitarias e instrumentadas.
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Herramientas de previsualización y depuración.
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}