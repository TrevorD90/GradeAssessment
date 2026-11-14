package com.gradeassessment.ui.screen.results

import com.gradeassessment.data.repository.ResultRepository
import com.gradeassessment.domain.agent.ReportGeneratorAgent
import com.gradeassessment.domain.agent.ResultsAnalysisAgent
import com.gradeassessment.domain.model.AnalysisReport
import com.gradeassessment.domain.model.AssessmentResult
import com.gradeassessment.domain.model.GrowthHistory
import com.gradeassessment.util.PdfExportHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ExportStatus {
    data object Idle : ExportStatus()
    data class Success(val path: String) : ExportStatus()
    data class Error(val message: String) : ExportStatus()
}

class ResultsViewModel(
    private val resultRepository: ResultRepository,
    private val analysisAgent: ResultsAnalysisAgent,
    private val reportGenerator: ReportGeneratorAgent
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _results = MutableStateFlow<List<AssessmentResult>>(emptyList())
    val results: StateFlow<List<AssessmentResult>> = _results.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedResult = MutableStateFlow<AssessmentResult?>(null)
    val selectedResult: StateFlow<AssessmentResult?> = _selectedResult.asStateFlow()

    private val _analysisReport = MutableStateFlow<AnalysisReport?>(null)
    val analysisReport: StateFlow<AnalysisReport?> = _analysisReport.asStateFlow()

    private val _growthHistory = MutableStateFlow<GrowthHistory?>(null)
    val growthHistory: StateFlow<GrowthHistory?> = _growthHistory.asStateFlow()

    private val _exportStatus = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportStatus: StateFlow<ExportStatus> = _exportStatus.asStateFlow()

    fun loadResults() {
        val all = resultRepository.getAllResults()
        _results.value = all
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun filteredResults(): List<AssessmentResult> {
        val query = _searchQuery.value.lowercase()
        if (query.isBlank()) return _results.value
        return _results.value.filter {
            it.student.firstName.lowercase().contains(query)
        }
    }

    fun selectResult(result: AssessmentResult) {
        // Saved results are stored without their missed-question lists; rebuild them from the
        // stored responses so the Detail View and PDF show them (they were always empty before).
        val hydrated = resultRepository.withMissedQuestions(result)
        _selectedResult.value = hydrated
        _analysisReport.value = analysisAgent.analyze(hydrated)
        _growthHistory.value = analysisAgent.growthHistory(hydrated)
    }

    fun clearSelection() {
        _selectedResult.value = null
        _analysisReport.value = null
        _growthHistory.value = null
        _exportStatus.value = ExportStatus.Idle
    }

    fun exportPdf(result: AssessmentResult) {
        scope.launch(Dispatchers.IO) {
            val hydrated = resultRepository.withMissedQuestions(result)
            val report = _analysisReport.value ?: analysisAgent.analyze(hydrated)
            val path = withContext(Dispatchers.Main) {
                PdfExportHelper.showSaveDialog(result.student.firstName)
            } ?: return@launch
            try {
                val growth = _growthHistory.value ?: analysisAgent.growthHistory(hydrated)
                reportGenerator.generatePdf(report, hydrated, path, growth)
                _exportStatus.value = ExportStatus.Success(path)
            } catch (e: Exception) {
                _exportStatus.value = ExportStatus.Error(e.message ?: "Unknown error")
            }
        }
    }
}
