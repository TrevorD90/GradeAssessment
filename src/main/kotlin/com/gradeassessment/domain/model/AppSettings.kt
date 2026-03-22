package com.gradeassessment.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppSettings {

    private val _includeCriticalThinkingInFull = MutableStateFlow(false)
    val includeCriticalThinkingInFull: StateFlow<Boolean> = _includeCriticalThinkingInFull.asStateFlow()

    private val _includeIqAssessmentInFull = MutableStateFlow(false)
    val includeIqAssessmentInFull: StateFlow<Boolean> = _includeIqAssessmentInFull.asStateFlow()

    fun setIncludeCriticalThinking(enabled: Boolean) {
        _includeCriticalThinkingInFull.value = enabled
    }

    fun setIncludeIqAssessment(enabled: Boolean) {
        _includeIqAssessmentInFull.value = enabled
    }

    /** Returns the 7 core subjects plus any enabled optional subjects appended at the end. */
    fun fullAssessmentSubjects(): List<Subject> {
        val subjects = Subject.CORE_SUBJECTS.toMutableList()
        if (_includeCriticalThinkingInFull.value) subjects.add(Subject.CRITICAL_THINKING)
        if (_includeIqAssessmentInFull.value) subjects.add(Subject.IQ_ASSESSMENT)
        return subjects
    }
}
