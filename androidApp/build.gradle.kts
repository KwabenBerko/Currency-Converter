@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
}

ktlint {
    filter {
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/kotlin/") }
    }
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.kwabenaberko.currencyconverter.android"
        minSdk = 22
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    namespace = "com.kwabenaberko.currencyconverter.android"
}

dependencies {
    implementation(projects.shared)
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation(libs.coroutines.core)
    implementation(libs.bundles.lifecycle)
    implementation(libs.kotlinx.collections)
    testImplementation(projects.sharedTest)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}
