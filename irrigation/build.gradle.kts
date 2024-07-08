import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.muss_coding.irrigation"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat.v140) // Updated artifact
    implementation(libs.androidx.constraintlayout) // Updated artifact
    testImplementation(libs.junit) // Updated artifact
    androidTestImplementation(libs.androidx.runner) // Updated artifact
    implementation(libs.androidx.cardview) // No change needed for CardView
    androidTestImplementation(libs.androidx.espresso.core.v340) // Updated artifact
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
}