// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false

    // 🔴 KSP alineado con Kotlin 2.0.x
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}
