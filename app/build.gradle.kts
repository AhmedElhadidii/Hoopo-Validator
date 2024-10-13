plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.evashadidi.validator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.evashadidi.validator"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    kapt {
        keepJavacAnnotationProcessors = true
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

//    implementation(libs.androidx.core.ktx)
    implementation("com.squareup.okhttp3:okhttp:4.11.0") {
        exclude(group = "com.intellij", module = "annotations")
    }

    implementation(libs.androidx.core) {
        exclude(group = "com.intellij", module = "annotations")
    }

//    implementation ("org.jetbrains:annotations:23.0.0")
//    constraints {
//        implementation("com.intellij:annotations") {
//            version {
//                strictly("23.0.0")
//            }
//        }
//    }

    implementation(libs.androidx.lifecycle.runtime.ktx){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.activity.compose){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(platform(libs.androidx.compose.bom)){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.ui){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.ui.graphics){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.ui.tooling.preview){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.material3){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation(libs.androidx.appcompat){
        exclude(group = "com.intellij", module = "annotations")
    }
    testImplementation(libs.junit){
        exclude(group = "com.intellij", module = "annotations")
    }
    androidTestImplementation(libs.androidx.junit){
        exclude(group = "com.intellij", module = "annotations")
    }
    androidTestImplementation(libs.androidx.espresso.core){
        exclude(group = "com.intellij", module = "annotations")
    }
    androidTestImplementation(platform(libs.androidx.compose.bom)){
        exclude(group = "com.intellij", module = "annotations")
    }
    androidTestImplementation(libs.androidx.ui.test.junit4){
        exclude(group = "com.intellij", module = "annotations")
    }
    debugImplementation(libs.androidx.ui.tooling){
        exclude(group = "com.intellij", module = "annotations")
    }
    debugImplementation(libs.androidx.ui.test.manifest){
        exclude(group = "com.intellij", module = "annotations")
    }

    // Room Dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1"){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation("androidx.room:room-ktx:2.6.1")

    // WorkManager Dependency
    implementation("androidx.work:work-runtime-ktx:2.8.1"){
        exclude(group = "com.intellij", module = "annotations")
    }

    // Coroutines Dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1"){
        exclude(group = "com.intellij", module = "annotations")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("org.jetbrains:annotations:25.0.0") // Use only the newer version


}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains:annotations:25.0.0")
    }
}

