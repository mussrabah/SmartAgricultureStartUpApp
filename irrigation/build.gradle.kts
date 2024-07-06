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
    implementation("androidx.appcompat:appcompat:1.4.0") // Updated artifact
    implementation("androidx.constraintlayout:constraintlayout:2.1.3") // Updated artifact
    testImplementation("junit:junit:4.13.2") // Updated artifact
    androidTestImplementation("androidx.test:runner:1.4.0") // Updated artifact
    implementation("androidx.cardview:cardview:1.0.0") // No change needed for CardView
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0") // Updated artifact
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
}