plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    id("com.google.devtools.ksp") version "2.3.3"
}

android {
    namespace = "com.example.conversormoedas"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.conversormoedas"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android e Kotlin
    implementation(libs.core.ktx.v1120)
    implementation(libs.lifecycle.runtime.ktx.v262)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose.v181)
    implementation(platform(libs.androidx.compose.bom.v20230300))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose.v275)

    // Networking (Retrofit e Serialização)
    val retrofitVersion = "2.9.0"
    val okhttpLoggingVersion = "4.11.0"
    implementation(libs.retrofit.v290)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json.v160)
    implementation(libs.logging.interceptor.v4110)

    // Injeção de Dependência (Koin)
    val koinVersion = "3.4.3"
    implementation(libs.koin.android.v343)
    implementation(libs.koin.androidx.compose.v343)


    // Database (Room) para Favoritos e Histórico (ATUALIZADO)
    val roomVersion = "2.6.0"
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Suporte a Coroutines
    // 2. ADICIONAR O COMPILADOR DO ROOM USANDO KSP
    ksp(libs.androidx.room.compiler.v260)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}