enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Currency_Converter"
include(":androidApp")
include(":converter")
include(":converterTest")
include(":shared")
