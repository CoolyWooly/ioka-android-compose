plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.android.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization.plugin)
}

android {
    namespace = "kz.ioka.android.ioka"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}


dependencies {

// AppCompat Core
    implementation(libs.appcompat.appcompat)
    implementation(libs.fragment.fragment.ktx)
// UI
    implementation("androidx.cardview:cardview:1.0.0")
    implementation (libs.constraintlayout.constraintlayout)
// Network
    implementation (libs.retrofit)
    implementation (libs.retrofit.converter.gson)
    implementation (libs.google.gson)
    implementation (libs.okhttp.urlconnection)
    implementation (libs.okhttp.logging.interceptor)
}
