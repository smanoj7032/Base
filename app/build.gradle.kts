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
    /**
     * AndroidX Core libraries
     * - core-ktx: Provides Kotlin extensions for Android core APIs.
     *   Why: Simplifies the use of core Android APIs with Kotlin.
     */
    implementation(libs.androidx.core.ktx)

    /**
     * AndroidX UI libraries
     * - appcompat: Provides backward-compatible versions of Android UI components.
     *   Why: Ensures compatibility with older Android versions.
     * - material: Google's Material Design components for Android.
     *   Why: Implements Material Design principles in the app.
     * - activity: Provides the Activity class and related APIs.
     *   Why: Essential for managing the lifecycle of activities.
     * - constraintlayout: Provides a flexible layout for complex user interfaces.
     *   Why: Enables creating complex layouts with a flat view hierarchy.
     * - core-splashscreen: Provides a customizable splash screen.
     *   Why: Adds a customizable splash screen for a better user experience.
     * - fragment-ktx: Provides Kotlin extensions for Android fragments.
     *   Why: Simplifies fragment operations with Kotlin.
     * - navigation: Libraries for implementing navigation in Android apps.
     *   Why: Simplifies navigation and passing data between destinations.
     */
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    /**
     * AndroidX Lifecycle libraries
     * - lifecycle-runtime-ktx: Kotlin extensions for Android lifecycle components.
     *   Why: Simplifies lifecycle-aware components with Kotlin.
     * - lifecycle-livedata-ktx: Kotlin extensions for LiveData.
     *   Why: Simplifies usage of LiveData with Kotlin.
     * - lifecycle-viewmodel-ktx: Kotlin extensions for ViewModel.
     *   Why: Simplifies ViewModel operations with Kotlin.
     * - activity-ktx: Provides Kotlin extensions for Activity.
     *   Why: Enhances Activity API usage with Kotlin.
     */
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)

    /**
     * Size Libraries
     * - sdp-android: A library for flexible sizes based on screen density.
     *   Why: Provides scalable dimension sizes for different screen densities.
     * - ssp-android: A library for flexible sizes based on screen size.
     *   Why: Provides scalable size values for different screen sizes.
     */
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    /**
     * Hilt for Dependency Injection
     * - hilt-android: Provides dependency injection.
     *   Why: Simplifies dependency injection and manages dependencies.
     * - hilt-android-compiler: Annotation processor for Hilt.
     *   Why: Generates the necessary code for Hilt dependency injection.
     */
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    /**
     * Google Places API
     * - places: Provides access to Google's Places API.
     *   Why: Allows integration of location-based features and services.
     */
    implementation(libs.places)

    /**
     * AndroidX Paging library
     * - paging-common-ktx: Kotlin extensions for common paging components.
     *   Why: Simplifies paging operations with Kotlin.
     * - paging-runtime-ktx: Kotlin extensions for runtime paging components.
     *   Why: Facilitates implementing paging in app's data layer.
     */
    implementation(libs.androidx.paging.common.ktx)
    implementation(libs.androidx.paging.runtime.ktx)

    /**
     * Image Loading Library
     * - glide: An image loading and caching library.
     *   Why: Efficiently loads and caches images in the app.
     */
    implementation(libs.glide)

    /**
     * RxJava for Reactive Programming
     * - rxjava: A library for composing asynchronous and event-based programs using observable sequences.
     *   Why: Facilitates reactive programming and handling asynchronous events.
     * - rxandroid: Android-specific bindings for RxJava.
     *   Why: Adds Android-specific bindings for RxJava.
     * - adapter-rxjava2: Retrofit adapter for RxJava2.
     *   Why: Integrates Retrofit with RxJava for network requests.
     */
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.adapter.rxjava2)

    /**
     * Networking Libraries
     * - retrofit: A type-safe HTTP client for Android.
     *   Why: Simplifies making network requests.
     * - converter-gson: A Retrofit converter for JSON to Java objects using Gson.
     *   Why: Converts JSON responses into Java objects.
     * - logging-interceptor: An OkHttp interceptor for logging HTTP requests and responses.
     *   Why: Logs network requests and responses for debugging purposes.
     */
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    /**
     * For Local Storage
     */
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    /**
     * Testing Libraries
     * - junit: Provides JUnit testing framework.
     *   Why: Essential for writing unit tests.
     * - androidx.junit: Provides AndroidX extensions for JUnit.
     *   Why: Facilitates writing Android-specific tests.
     * - espresso-core: Provides the Espresso testing framework for Android UI.
     *   Why: Enables writing UI tests for Android.
     */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}