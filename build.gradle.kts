// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    //id("com.google.dagger.hilt.android") version "2.44" apply false
    alias(libs.plugins.hiltAndroidPlugin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
    alias(libs.plugins.androidLibrary) apply false
}