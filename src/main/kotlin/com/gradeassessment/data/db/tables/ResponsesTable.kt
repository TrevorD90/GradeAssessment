package com.gradeassessment.data.db.tables

import org.jetbrains.exposed.sql.Table

object ResponsesTable : Table("responses") {
    val id = long("id").autoIncrement()
    val sessionId = long("session_id").references(SessionsTable.id)
    val subject = varchar("subject", 50)
    val subtest = varchar("subtest", 50).nullable()
    val questionId = varchar("question_id", 255)
    val studentAnswer = text("student_answer")
    val correct = bool("correct")
    val timeToAnswerMs = long("time_to_answer_ms")
    val hintUsed = bool("hint_used").default(false)
    val thetaAtTime = double("theta_at_time")
    val questionIndex = integer("question_index")

    override val primaryKey = PrimaryKey(id)
}
