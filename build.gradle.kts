// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Các plugin hiện có
    alias(libs.plugins.android.application) apply false


    // Thêm plugin Google services Gradle
    id("com.google.gms.google-services") version "4.4.2" apply false
}
