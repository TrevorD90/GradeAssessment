package com.gradeassessment.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.gradeassessment.ui.navigation.AppScreen
import com.gradeassessment.ui.navigation.MainTab
import com.gradeassessment.ui.screen.assessment.*
import com.gradeassessment.ui.screen.results.ExportStatus
import com.gradeassessment.ui.screen.results.ResultsListScreen
import com.gradeassessment.ui.screen.results.ResultsViewModel
import com.gradeassessment.ui.screen.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun MainWindow() {
    val assessmentViewModel: AssessmentViewModel = koinInject()
    val resultsViewModel: ResultsViewModel = koinInject()

    var selectedTab by remember { mutableStateOf(MainTab.ASSESSMENT) }

    val screen by assessmentViewModel.screen.collectAsState()
    val currentQuestion by assessmentViewModel.currentQuestion.collectAsState()
    val currentSubject by assessmentViewModel.currentSubject.collectAsState()
    val questionIndex by assessmentViewModel.questionIndex.collectAsState()
    val timeRemaining by assessmentViewModel.timeRemaining.collectAsState()
    val assessmentResult by assessmentViewModel.assessmentResult.collectAsState()
    val subjectScores by assessmentViewModel.subjectScores.collectAsState()
    val assessmentExportStatus by assessmentViewModel.exportStatus.collectAsState()
    val selectedSubjects by assessmentViewModel.selectedSubjects.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(assessmentExportStatus) {
        when (val status = assessmentExportStatus) {
            is ExportStatus.Success -> snackbarHostState.showSnackbar("PDF saved to ${status.path}")
            is ExportStatus.Error -> snackbarHostState.showSnackbar("Export failed: ${status.message}")
            ExportStatus.Idle -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { scaffoldPadding ->
    Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding)) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            Tab(
                selected = selectedTab == MainTab.ASSESSMENT,
                onClick = { selectedTab = MainTab.ASSESSMENT },
                text = { Text("Assessment") },
                icon = { Icon(Icons.Default.Assessment, contentDescription = "New Assessment tab") }
            )
            Tab(
                selected = selectedTab == MainTab.RESULTS,
                onClick = {
                    selectedTab = MainTab.RESULTS
                    resultsViewModel.loadResults()
                },
                text = { Text("Results") },
                icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Results tab") }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                MainTab.ASSESSMENT -> {
                    when (val currentScreen = screen) {
                        is AppScreen.Welcome -> {
                            WelcomeScreen(
                                onFullAssessmentClick = { assessmentViewModel.startFullAssessment() },
                                onSingleSubjectClick = { assessmentViewModel.startSingleSubjectAssessment() },
                                onSettingsClick = { assessmentViewModel.navigateToSettings() }
                            )
                        }
                        is AppScreen.Settings -> {
                            SettingsScreen(
                                onBack = { assessmentViewModel.resetToWelcome() }
                            )
                        }
                        is AppScreen.StudentProfile -> {
                            StudentProfileScreen(
                                onSubmit = { name, age ->
                                    assessmentViewModel.startAssessment(name, age)
                                }
                            )
                        }
                        is AppScreen.SubjectSelection -> {
                            SubjectSelectionScreen(
                                onConfirm = { subjects ->
                                    assessmentViewModel.confirmSubjectSelection(subjects)
                                }
                            )
                        }
                        is AppScreen.Instructions -> {
                            InstructionsScreen(
                                studentName = "",
                                selectedSubjects = selectedSubjects,
                                onBeginClick = { assessmentViewModel.beginAssessment() }
                            )
                        }
                        is AppScreen.ActiveAssessment -> {
                            ActiveAssessmentScreen(
                                question = currentQuestion,
                                subject = currentSubject,
                                questionIndex = questionIndex,
                                timeRemaining = timeRemaining,
                                onSubmitAnswer = { q, answer, answers, ordered, fluency ->
                                    assessmentViewModel.submitAnswer(q, answer, answers, ordered, fluency)
                                }
                            )
                        }
                        is AppScreen.SubjectTransition -> {
                            SubjectTransitionScreen(
                                completedSubject = currentScreen.completedSubject,
                                scoreBundle = subjectScores[currentScreen.completedSubject],
                                nextSubject = currentScreen.nextSubject,
                                onContinueClick = { assessmentViewModel.startNextSubject() }
                            )
                        }
                        is AppScreen.Completion -> {
                            CompletionScreen(
                                result = assessmentResult,
                                onExportPdf = {
                                    val result = assessmentResult ?: return@CompletionScreen
                                    assessmentViewModel.exportPdf(result)
                                },
                                onNewAssessment = { assessmentViewModel.resetToWelcome() }
                            )
                        }
                    }
                }
                MainTab.RESULTS -> {
                    ResultsListScreen(viewModel = resultsViewModel)
                }
            }
        }
    }
    }
}
