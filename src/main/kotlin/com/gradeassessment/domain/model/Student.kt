package com.gradeassessment.domain.model

data class Student(
    val firstName: String,
    val age: Int
) {
    val expectedGrade: Double get() = (age - 5).coerceAtLeast(0).toDouble()
}
