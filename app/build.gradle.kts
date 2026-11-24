import java.util.Properties

// Plugins aplicados al módulo de la aplicación.
plugins {
    jacoco
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // Plugin KSP necesario para generar código de Room
}

val firma = Properties().apply {
    val archivo = rootProject.file("signing.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
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

        versionCode = 4
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (firma.getProperty("storeFile") != null) {
            create("entrega") {
                storeFile = rootProject.file(firma.getProperty("storeFile"))
                storePassword = firma.getProperty("storePassword")
                keyAlias = firma.getProperty("keyAlias")
                keyPassword = firma.getProperty("keyPassword")
            }
        }
    }

    // Tipos de compilación (Release y Debug).
    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            // Se instala junto a la versión anterior, conservando su entrega y sus datos.
            applicationIdSuffix = ".integracion"
            resValue("string", "app_name", "Master Martini API")
            if (firma.getProperty("storeFile") != null) signingConfig = signingConfigs.getByName("entrega")
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
        resValues = true
    }
}

// Cobertura de la lógica añadida en esta entrega; no se presenta como cobertura de toda la UI.
tasks.register<JacocoReport>("coberturaModulos") {
    dependsOn("testDebugUnitTest")
    reports { xml.required.set(true); html.required.set(true) }
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
        include("**/data/repository/*RemotoRepository*.class", "**/data/repository/CursoRepository*.class",
            "**/data/repository/RecetaRepository*.class", "**/data/repository/VideoRepository*.class",
            "**/data/repository/RecetaExternaRepository*.class", "**/ui/viewmodels/ModuloViewModel*.class",
            "**/ui/viewmodels/CursosViewModel*.class", "**/ui/viewmodels/RecetasViewModel*.class",
            "**/ui/viewmodels/VideosViewModel*.class", "**/ui/viewmodels/RecetaExternaViewModel*.class",
            "**/utils/ValidacionesContenido*.class")
    })
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"))
}

tasks.withType<Test>().configureEach {
    systemProperty("mastermartini.live", providers.gradleProperty("liveApi").orElse("false").get())
    systemProperty("mastermartini.backendLive", providers.gradleProperty("liveBackend").orElse("false").get())
}

// Informe de toda la lógica móvil: ViewModels, repositorios, modelos con comportamiento y utilidades.
tasks.register<JacocoReport>("coberturaLogica") {
    dependsOn("testDebugUnitTest")
    reports { xml.required.set(true); html.required.set(true) }
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
        include("**/ui/viewmodels/*ViewModel*.class", "**/data/repository/*.class", "**/utils/*.class",
            "**/domain/model/Contenido*.class", "**/domain/model/Curso*.class",
            "**/domain/model/Receta*.class", "**/domain/model/Video*.class", "**/data/remote/NetworkModule*.class", "**/data/remote/ContenidoRespuesta*.class")
    })
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"))
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

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.mockito:mockito-core:5.20.0")
    // Las pruebas del compañero en main usan MockK.
    testImplementation("io.mockk:mockk:1.13.12")

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
