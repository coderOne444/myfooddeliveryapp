plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.jetpackcomposenew"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.jetpackcomposenew"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"  // Make sure this aligns with your Compose version
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (Bill of Materials) to ensure compatibility across Compose libraries
    implementation(platform(libs.androidx.compose.bom))

    // UI and foundation libraries for Compose
    implementation("androidx.compose.ui:ui:1.5.1")  // Updated to latest stable
    implementation("androidx.compose.foundation:foundation:1.5.1")  // Updated to latest stable
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")  // Updated to latest stable
    implementation("androidx.compose.ui:ui-tooling:1.5.1")  // Ensure latest version for debugging

    // Material 3 and extended icons
    implementation("androidx.compose.material3:material3:1.1.1")  // Use the latest stable version
    implementation("androidx.compose.material:material-icons-extended:1.5.1")  // Align with Compose versions

    // Navigation component for Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.1")  // Updated to latest stable

    // Google Play services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation(libs.play.services.maps)

    // AndroidX libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Unit and UI testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")  // Align with Compose versions

    // Debugging tools for Compose
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.1")  // Align with Compose versions
    debugImplementation(libs.androidx.ui.test.manifest)
}
