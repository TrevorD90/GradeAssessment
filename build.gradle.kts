import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

group = "com.gradeassessment"
version = "1.0.0"

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
            packageVersion = "1.0.1"
            description = "Adaptive Academic Assessment Platform"
            vendor = "Grade Assessment"

            modules(
                "java.sql",
                "java.naming",
                "jdk.unsupported"
            )

            windows {
                shortcut = true
                dirChooser = true
                menuGroup = "Grade Assessment"
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
