package com.gradeassessment.ui.screen.results

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gradeassessment.ui.components.ResultsTable
import com.gradeassessment.ui.components.SearchFilterBar

@Composable
fun ResultsListScreen(
    viewModel: ResultsViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedResult by viewModel.selectedResult.collectAsState()
    val analysisReport by viewModel.analysisReport.collectAsState()
    val growthHistory by viewModel.growthHistory.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadResults()
    }

    LaunchedEffect(exportStatus) {
        when (val status = exportStatus) {
            is ExportStatus.Success -> snackbarHostState.showSnackbar("PDF saved to ${status.path}")
            is ExportStatus.Error -> snackbarHostState.showSnackbar("Export failed: ${status.message}")
            ExportStatus.Idle -> {}
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(modifier = modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Text(
                text = "Assessment Results",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SearchFilterBar(
                searchQuery = searchQuery,
                onSearchChanged = { viewModel.updateSearch(it) }
            )

            Spacer(Modifier.height(16.dp))

            val filtered = viewModel.filteredResults()

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No assessments completed yet."
                        else "No results match your search.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ResultsTable(
                    results = filtered,
                    onResultClick = { viewModel.selectResult(it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (selectedResult != null) {
        ResultDetailDialog(
            result = selectedResult!!,
            analysisReport = analysisReport,
            growthHistory = growthHistory,
            onExportPdf = { viewModel.exportPdf(selectedResult!!) },
            onDismiss = { viewModel.clearSelection() }
        )
    }
}
