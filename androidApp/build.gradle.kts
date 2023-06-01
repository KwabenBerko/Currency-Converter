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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    namespace = "com.kwabenaberko.currencyconverter.android"
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.material)
    implementation(libs.activityCompose)
    implementation(libs.compose.ui.preview)
    implementation(libs.viewModelCompose)
    implementation(libs.kmmViewModel)
    implementation(libs.navigationCompose)
    implementation(libs.accompanist.systemuiController)
    implementation(libs.accompanist.navigationAnimation)
    implementation(libs.workmanager)
    implementation(libs.coroutines.core)
    implementation(libs.bundles.lifecycle)
    implementation(libs.kotlinx.collections)
    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}
