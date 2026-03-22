package com.gradeassessment.data.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SessionsTable : Table("sessions") {
    val id = long("id").autoIncrement()
    val studentFirstName = varchar("student_first_name", 255)
    val studentAge = integer("student_age")
    val startTime = timestamp("start_time")
    val endTime = timestamp("end_time").nullable()
    val status = varchar("status", 50)
    val durationMs = long("duration_ms").default(0)

    override val primaryKey = PrimaryKey(id)
}
