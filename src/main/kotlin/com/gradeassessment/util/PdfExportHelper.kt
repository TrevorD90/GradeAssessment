package com.gradeassessment.util

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object PdfExportHelper {

    fun showSaveDialog(studentName: String): String? {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val defaultFileName = "${studentName}_Assessment_$date.pdf"

        val dialog = FileDialog(null as Frame?, "Save PDF Report", FileDialog.SAVE)
        dialog.file = defaultFileName
        dialog.setFilenameFilter { _, name -> name.endsWith(".pdf") }
        dialog.isVisible = true

        val directory = dialog.directory
        val file = dialog.file

        if (directory != null && file != null) {
            val path = File(directory, file).absolutePath
            return if (path.endsWith(".pdf")) path else "$path.pdf"
        }
        return null
    }
}
