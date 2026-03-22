package com.gradeassessment.di

import com.gradeassessment.data.db.DatabaseFactory
import com.gradeassessment.data.db.dao.ResponseDao
import com.gradeassessment.data.db.dao.ResultDao
import com.gradeassessment.data.db.dao.SessionDao
import com.gradeassessment.data.questionbank.QuestionRepository
import com.gradeassessment.data.repository.ResultRepository
import com.gradeassessment.data.repository.SessionRepository
import com.gradeassessment.domain.agent.*
import com.gradeassessment.ui.screen.assessment.AssessmentViewModel
import com.gradeassessment.ui.screen.results.ResultsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.module

val appModule = module {
    single {
        DatabaseFactory.init()
        DatabaseFactory
    }

    // DAOs depend on DatabaseFactory to ensure DB is initialized before use
    single { get<DatabaseFactory>(); SessionDao() }
    single { get<DatabaseFactory>(); ResponseDao() }
    single { get<DatabaseFactory>(); ResultDao() }

    single {
        val repo = QuestionRepository()
        // Load questions on a background thread to avoid blocking UI startup
        CoroutineScope(Dispatchers.IO).launch {
            repo.loadAll()
        }
        repo
    }

    single { SessionRepository(get(), get()) }
    single { ResultRepository(get()) }

    single { AdaptiveQuestionAgent(get()) }
    single { AssessmentEngineAgent(get(), get(), get()) }
    single { OrchestratorAgent(get(), get(), get()) }
    single { ResultsAnalysisAgent(get()) }
    single { ReportGeneratorAgent() }

    factory { AssessmentViewModel(get(), get(), get(), get()) }
    factory { ResultsViewModel(get(), get(), get()) }
}
