package com.gradeassessment.domain.model

enum class GradeLevel(val display: String, val numericValue: Double) {
    BELOW_K("Below K", -0.5),
    K("Kindergarten", 0.0),
    G1("1st Grade", 1.0),
    G2("2nd Grade", 2.0),
    G3("3rd Grade", 3.0),
    G4("4th Grade", 4.0),
    G5("5th Grade", 5.0),
    G6("6th Grade", 6.0),
    G7("7th Grade", 7.0),
    G8("8th Grade", 8.0),
    G9("9th Grade", 9.0),
    G10("10th Grade", 10.0),
    ABOVE_G10("Above 10th", 11.0);

    companion object {
        fun fromGradeEquivalent(ge: Double): GradeLevel = when {
            ge < 0.0 -> BELOW_K
            ge < 0.5 -> K
            ge < 1.5 -> G1
            ge < 2.5 -> G2
            ge < 3.5 -> G3
            ge < 4.5 -> G4
            ge < 5.5 -> G5
            ge < 6.5 -> G6
            ge < 7.5 -> G7
            ge < 8.5 -> G8
            ge < 9.5 -> G9
            ge < 10.5 -> G10
            else -> ABOVE_G10
        }
    }
}
