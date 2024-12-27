plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appsnacks"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appsnacks"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        encoding = "UTF-8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val nav_version = "2.7.6"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    implementation(libs.appcompat)

    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    // Add Firebase Analytics
    implementation(libs.firebase.analytics)
    // Firebase Realtime Database
    implementation(libs.firebase.database)
    //firebase auth
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    // Phone Auth
    implementation ("com.google.android.gms:play-services-safetynet:18.0.1")
    //menu
    implementation("com.google.android.material:material:1.11.0")
    implementation ("androidx.fragment:fragment:1.6.2")
    //EdgeToEdge
    implementation ("androidx.core:core:1.12.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    //check login :
    implementation ("com.google.code.gson:gson:2.8.9")
    //
    implementation ("com.squareup.picasso:picasso:2.8")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    // Retrofit for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")



}