import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.androidx.navigation)
}

android {
    namespace = "com.manoj.baseproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.manoj.baseproject"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        val properties = Properties()

        val localPropertiesFile = project.rootProject.file("apikeys.properties")
       properties.load(localPropertiesFile.inputStream())
        val apiKey: String = properties.getProperty("APP_ID") ?: ""


        this.buildConfigField("String","APP_ID", apiKey)
        this.buildConfigField("String","BASE_URL","\"https://dummyapi.io/\"")
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
    dataBinding {
        this.enable=true
    }
    buildFeatures {
        viewBinding =true
        buildConfig = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)

    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)


    implementation(libs.places)

    implementation(libs.androidx.paging.common.ktx)
    implementation(libs.androidx.paging.runtime.ktx)

    implementation(libs.glide)

    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.adapter.rxjava2)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}