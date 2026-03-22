package com.gradeassessment.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Subject(val displayName: String) {
    MATH("Math"),
    READING("Reading"),
    WRITING("Writing"),
    SCIENCE("Science"),
    ARTS("Arts"),
    WORLD_HISTORY("World History"),
    SOCIAL_STUDIES("Social Studies"),
    CRITICAL_THINKING("Critical Thinking"),
    IQ_ASSESSMENT("IQ Assessment");

    companion object {
        val CORE_SUBJECTS = listOf(MATH, READING, WRITING, SCIENCE, ARTS, WORLD_HISTORY, SOCIAL_STUDIES)
        val OPTIONAL_SUBJECTS = listOf(CRITICAL_THINKING, IQ_ASSESSMENT)
    }
}
