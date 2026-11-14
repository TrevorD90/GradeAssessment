import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

group = "com.gradeassessment"
version = "1.1.0"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.sqlite.jdbc)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.kotlinx.datetime)

    implementation(libs.slf4j.simple)
    implementation(libs.openpdf)

    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "com.gradeassessment.MainKt"

        jvmArgs += listOf(
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-opens", "java.base/java.io=ALL-UNNAMED"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GradeAssessment"
            // MSI requires MAJOR.MINOR.BUILD with MAJOR <= 255. Bump for every release.
            packageVersion = "1.1.0"
            description = "Adaptive Academic Assessment Platform"
            vendor = "Grade Assessment"

            // Java modules bundled into the private runtime. Checked with `gradlew suggestRuntimeModules`;
            // a missing module makes the installed app crash on launch even though it runs from the IDE.
            modules(
                "java.instrument",
                "java.sql",
                "java.naming",
                "jdk.unsupported"
            )

            windows {
                shortcut = true          // desktop shortcut
                menu = true              // Start menu entry
                menuGroup = "Grade Assessment"
                dirChooser = true
                // [DECISION: per-user install - no administrator rights needed, installs under the
                //  user's AppData. Student data lives in ~/.grade-assessment and survives upgrades.]
                perUserInstall = true
                // Must never change: lets a newer MSI replace an older one instead of installing twice.
                upgradeUuid = "6a7ba1b4-0f62-4e63-9091-7ea2b99ef4e2"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
