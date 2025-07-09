plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.android.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt)
}
android {
    namespace = "kz.ioka.android"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "kz.ioka.android"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "TEST_API_KEY",
                "\"shp_GA9Y41H1EJ_test_public_60e22bb99d75650ad1d3e54064461152cb9a954d43ea4629d6931703d5ef87f8\""
            )
            buildConfigField(
                "String",
                "RELEASE_API_KEY",
                "\"shp_GA9Y41H1EJ_test_public_60e22bb99d75650ad1d3e54064461152cb9a954d43ea4629d6931703d5ef87f8\""
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

    android.buildFeatures.buildConfig = true
}
dependencies {
    implementation(project(":ioka"))

    implementation(libs.core.ktx)
    implementation(libs.appcompat.appcompat)
    implementation(libs.material.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation(libs.constraintlayout.constraintlayout)

    implementation(libs.fragment.fragment.ktx)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.google.gson)
    implementation(libs.okhttp.urlconnection)
    implementation(libs.okhttp.logging.interceptor)

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
}
