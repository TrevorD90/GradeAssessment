package com.gradeassessment

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.gradeassessment.di.appModule
import com.gradeassessment.ui.screen.MainWindow
import com.gradeassessment.ui.theme.GradeAssessmentTheme
import org.koin.compose.KoinApplication

fun main() = application {
    KoinApplication(application = { modules(appModule) }) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Grade Assessment",
            state = rememberWindowState(placement = WindowPlacement.Maximized)
        ) {
            GradeAssessmentTheme {
                MainWindow()
            }
        }
    }
}
