@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
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
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
    namespace = "com.kwabenaberko.currencyconverter.android"
}

dependencies {
    implementation(projects.converter)
    implementation(projects.converterTest)
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.compose.material3:material3:1.1.0-alpha03")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.7.30-beta")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.7.30-beta")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.28.0")
    implementation("com.airbnb.android:lottie-compose:5.2.0")
    implementation(libs.coroutines.core)
    implementation(libs.bundles.lifecycle)
    implementation(libs.kotlinx.collections)
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.2")
    testImplementation(projects.converterTest)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}
