plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    //kotlin("plugin.serialization") version "2.2.10"
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

kotlin {
    jvmToolchain(17)
}


android {
    namespace = "com.example.followme02"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.followme02"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //implementation(platform("io.github.jan-tennert.supabase:bom:3.4.0"))
    //implementation("io.github.jan-tennert.supabase:postgrest-kt")
    //implementation("io.github.jan-tennert.supabase:supabase-kt")
    //implementation("io.ktor:ktor-client-android:3.4.0")
    //implementation(platform("io.github.jan.supabase:bom:3.0.0"))
    //implementation("io.github.jan.supabase:supabase-kt")
    //implementation("io.github.jan.supabase:postgrest-kt")
    //implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.0")
    implementation("io.github.jan-tennert.supabase:supabase-kt:3.0.0")
    implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.7.0")
    //implementation("com.google.dagger:hilt-android:2.57.1")
    //ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.text.google.fonts)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}