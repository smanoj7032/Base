import java.text.SimpleDateFormat
import java.util.Date
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
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.manoj.baseproject"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("apikeys.properties")
        properties.load(localPropertiesFile.inputStream())
        val apiKey: String = properties.getProperty("APP_ID") ?: ""

        this.buildConfigField("String", "APP_ID", apiKey)
        this.buildConfigField("String", "BASE_URL", "\"https://dummyapi.io/\"")
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
        this.enable = true
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    applicationVariants.configureEach {
        /**
         * Retrieve the build variant information
         * - builtType: Name of the build type (e.g., debug, release).
         * - flavor: Name of the product flavor (if any).
         * - versionCode: Version code of the app.
         */
        val variant = this
        val builtType = variant.buildType.name
        val flavor = variant.flavorName
        val versionCode = variant.versionCode

        /**
         * Generate a formatted date string
         * - formattedDate: Current date and time formatted as "dd-MMM-yyyy hh:mm a".
         *   Why: This adds a timestamp to the output APK file name for version tracking.
         */
        val formattedDate = SimpleDateFormat("ddMMMyy_HHmm").format(Date())

        /**
         * Create a custom output file name
         * - outputFileName: Custom name for the output APK file including build type, flavor, version code, and date.
         *   Why: This helps in distinguishing different builds easily by including relevant information in the file name.
         */
        val outputFileName = "Base $builtType $flavor $versionCode $formattedDate.apk"

        /**
         * Assign the custom file name to the variant's output
         * - outputs: Iterate over the outputs of the variant and set the custom file name.
         *   Why: This step ensures that the APK file is generated with the custom file name.
         */
        variant.outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = outputFileName
            }
    }
}

dependencies {
    implementation(libs.bundles.androidx.main)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.bundles.lifecycle.main)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.places)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.bundles.dimens)
    implementation(libs.glide)
    implementation(libs.bundles.rxjava)
    implementation(libs.bundles.square.retrofit2)
    implementation(libs.logging.interceptor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
}