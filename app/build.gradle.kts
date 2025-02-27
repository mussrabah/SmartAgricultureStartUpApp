plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    //id("com.google.dagger.hilt.android")
    alias(libs.plugins.hiltAndroidPlugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleGmsGoogleServices)
    //id("kotlin-kapt")

}

android {
    namespace = "com.muss_coding.smartagriculturestartupapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.muss_coding.smartagriculturestartupapp"
        minSdk = 28
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit)
    implementation(libs.moshi.converter)
    implementation(libs.moshi.kotlin)
    implementation(libs.ok.http)
    implementation(libs.ok.http.logging.interceptor)
    implementation(libs.hilt.android.navigation.compose)
    implementation(libs.rendering)
    implementation(project(":soil_type"))
    implementation(project(":soil_type"))
    implementation(project(":crop_recommendation"))
    implementation(project(":irrigation"))
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.google.play.services)
    implementation("com.google.accompanist:accompanist-permissions:0.31.5-beta")

    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    implementation(libs.coil.compose)

}
// Allow references to generated code
//kapt {
//    correctErrorTypes = true
//    arguments {
//        arg("jdk.compiler.args", "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
//    }
//}