package com.gradeassessment.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IrtParameters(
    val a: Double = 1.0,
    val b: Double = 0.0,
    val c: Double = 0.25
)
