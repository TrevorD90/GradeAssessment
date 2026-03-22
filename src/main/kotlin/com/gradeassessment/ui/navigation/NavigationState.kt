package com.gradeassessment.ui.navigation

import com.gradeassessment.domain.model.Subject

sealed class AppScreen {
    data object Welcome : AppScreen()
    data object Settings : AppScreen()
    data object StudentProfile : AppScreen()
    data object SubjectSelection : AppScreen()
    data object Instructions : AppScreen()
    data object ActiveAssessment : AppScreen()
    data class SubjectTransition(val completedSubject: Subject, val nextSubject: Subject?) : AppScreen()
    data object Completion : AppScreen()
}

enum class MainTab {
    ASSESSMENT,
    RESULTS
}
