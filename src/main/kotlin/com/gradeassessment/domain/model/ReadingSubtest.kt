package com.gradeassessment.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReadingSubtest {
    LETTER_NAMING,
    PHONEME_SEGMENTATION,
    NONSENSE_WORD,
    COMPREHENSION
}
