import org.gradle.internal.os.OperatingSystem

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.gradlePlugin).apply(false)
    alias(libs.plugins.kotlin.serialization).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.versions)
    alias(libs.plugins.catalogUpdate)
}


allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register<Copy>("installGitHooks") {
    val isWindowsOS = OperatingSystem.current().isWindows
    if (!isWindowsOS){
        val fromDir = "${rootProject.rootDir}/.githooks"
        val toDir = "${rootProject.rootDir}/.git/hooks"

        from(fromDir)
        into(toDir)

        doLast {
            Runtime.getRuntime().exec("chmod a+x $toDir")
        }
    }
}
