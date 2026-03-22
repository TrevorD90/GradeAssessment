package com.gradeassessment.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class QuestionType {
    @SerialName("MULTIPLE_CHOICE") MULTIPLE_CHOICE,
    @SerialName("MULTI_SELECT") MULTI_SELECT,
    @SerialName("FILL_IN_BLANK") FILL_IN_BLANK,
    @SerialName("CLOZE") CLOZE,
    @SerialName("ORDERING") ORDERING,
    @SerialName("PASSAGE_BASED") PASSAGE_BASED,
    @SerialName("TIMED_FLUENCY") TIMED_FLUENCY
}
