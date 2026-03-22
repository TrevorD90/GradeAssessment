package com.gradeassessment.domain.agent

import com.gradeassessment.domain.model.*
import com.gradeassessment.domain.scoring.ScoringHelper
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream

class ReportGeneratorAgent {

    fun generatePdf(report: AnalysisReport, result: AssessmentResult, outputPath: String): File {
        val file = File(outputPath)
        val fos = FileOutputStream(file)
        val document = Document(PageSize.LETTER, 36f, 36f, 36f, 36f)
        try {
            PdfWriter.getInstance(document, fos)
            document.open()

            addHeader(document, result)
            addReportGuide(document)
            addOverallSummary(document, report)
            addSubjectScoreTable(document, result, report)
            addCriticalThinkingSection(document, result)
            addIqAssessmentSection(document, result)
            addMissedQuestionsSection(document, result)
            addStandardsMasterySection(document, report)
            addRecommendationsSection(document, report)
            addStandardsCodeReference(document, result)
        } finally {
            try { document.close() } catch (_: Exception) {}
            try { fos.close() } catch (_: Exception) {}
        }
        return file
    }

    private fun addHeader(document: Document, result: AssessmentResult) {
        val titleFont = Font(Font.HELVETICA, 20f, Font.BOLD)
        val subtitleFont = Font(Font.HELVETICA, 12f, Font.NORMAL)

        val title = Paragraph("Grade Assessment Report", titleFont)
        title.alignment = Element.ALIGN_CENTER
        document.add(title)

        document.add(Paragraph("\n"))

        val info = Paragraph()
        info.add(Chunk("Student: ", Font(Font.HELVETICA, 12f, Font.BOLD)))
        info.add(Chunk("${result.student.firstName}\n", subtitleFont))
        info.add(Chunk("Age: ", Font(Font.HELVETICA, 12f, Font.BOLD)))
        info.add(Chunk("${result.student.age}\n", subtitleFont))
        info.add(Chunk("Date: ", Font(Font.HELVETICA, 12f, Font.BOLD)))
        val date = result.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
        info.add(Chunk("$date\n", subtitleFont))
        info.add(Chunk("Duration: ", Font(Font.HELVETICA, 12f, Font.BOLD)))
        val minutes = result.durationMs / 60000
        info.add(Chunk("${minutes} minutes\n", subtitleFont))
        document.add(info)

        document.add(Paragraph("\n"))
    }

    private fun addReportGuide(document: Document) {
        val sectionFont = Font(Font.HELVETICA, 14f, Font.BOLD)
        val bodyFont = Font(Font.HELVETICA, 10f, Font.NORMAL)
        val boldFont = Font(Font.HELVETICA, 10f, Font.BOLD)

        document.add(Paragraph("How to Read This Report", sectionFont))
        document.add(Paragraph("\n"))

        val guide = Paragraph()
        guide.add(Chunk("This assessment adapts to the student's ability level using Item Response Theory (IRT). " +
                "Questions get harder or easier based on performance. Here is what each metric means:\n\n", bodyFont))

        guide.add(Chunk("Scaled Score: ", boldFont))
        guide.add(Chunk("A standardized score from 0-100 that represents overall performance in a subject. " +
                "Higher scores indicate stronger performance.\n\n", bodyFont))

        guide.add(Chunk("Grade Equivalent (GE): ", boldFont))
        guide.add(Chunk("The grade level at which the student is performing. For example, a GE of 3.5 means " +
                "the student performs at a mid-3rd-grade level. A negative GE (e.g., -0.5) means performance " +
                "is below kindergarten level.\n\n", bodyFont))

        guide.add(Chunk("Suggested Grade Level: ", boldFont))
        guide.add(Chunk("The grade placement that best matches the student's demonstrated ability, " +
                "derived from the Grade Equivalent score.\n\n", bodyFont))

        guide.add(Chunk("Risk Tier: ", boldFont))
        guide.add(Chunk("Indicates how the student's performance compares to grade-level expectations:\n", bodyFont))
        guide.add(Chunk("  - At/Above Benchmark: ", boldFont))
        guide.add(Chunk("Performing at or above expected grade level.\n", bodyFont))
        guide.add(Chunk("  - Below Benchmark: ", boldFont))
        guide.add(Chunk("0.5 to 1.5 grade levels behind. Targeted support recommended.\n", bodyFont))
        guide.add(Chunk("  - Well Below Benchmark: ", boldFont))
        guide.add(Chunk("More than 1.5 grade levels behind. Intensive intervention recommended.\n\n", bodyFont))

        guide.add(Chunk("Theta: ", boldFont))
        guide.add(Chunk("The IRT ability estimate on a standard scale (typically -4 to +4). " +
                "Higher values indicate greater ability. This is the raw measure used to compute all other scores.\n\n", bodyFont))

        guide.add(Chunk("Standard Error (SE): ", boldFont))
        guide.add(Chunk("The precision of the theta estimate. Lower SE values (closer to 0) mean more reliable results. " +
                "An SE above 1.0 suggests the estimate may be less precise.\n\n", bodyFont))

        guide.add(Chunk("Rank: ", boldFont))
        guide.add(Chunk("The student's relative strength across assessed subjects. Rank 1 is the strongest subject.\n\n", bodyFont))

        guide.add(Chunk("Standard Codes (e.g., L.1.1, W.4.3): ", boldFont))
        guide.add(Chunk("These reference Common Core State Standards. The letter indicates the domain " +
                "(L = Language, W = Writing, RI = Reading Informational, RL = Reading Literature, " +
                "NBT = Numbers & Operations, OA = Operations & Algebraic Thinking, G = Geometry, " +
                "MD = Measurement & Data). The numbers indicate grade level and standard number. " +
                "See the Standards Reference at the end of this report for details.\n", bodyFont))

        document.add(guide)
        document.add(Paragraph("\n"))
    }

    private fun addOverallSummary(document: Document, report: AnalysisReport) {
        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        document.add(Paragraph("Overall Summary", sectionFont))
        document.add(Paragraph("\n"))

        val bodyFont = Font(Font.HELVETICA, 11f, Font.NORMAL)
        val boldFont = Font(Font.HELVETICA, 11f, Font.BOLD)

        val gradeLevel = GradeLevel.fromGradeEquivalent(report.overallGradeEquivalent)

        val summary = Paragraph()
        summary.add(Chunk("Overall Grade Equivalent: ", boldFont))
        summary.add(Chunk("%.1f\n".format(report.overallGradeEquivalent), bodyFont))
        summary.add(Chunk("Suggested Grade Level: ", boldFont))
        summary.add(Chunk("${gradeLevel.display}\n", bodyFont))
        summary.add(Chunk("Overall Risk Tier: ", boldFont))
        summary.add(Chunk("${report.overallRiskTier.display}\n", bodyFont))
        if (report.strongestSubject != null) {
            summary.add(Chunk("Strongest Subject: ", boldFont))
            summary.add(Chunk("${report.strongestSubject.displayName}\n", bodyFont))
        }
        if (report.weakestSubject != null) {
            summary.add(Chunk("Needs Improvement: ", boldFont))
            summary.add(Chunk("${report.weakestSubject.displayName}\n", bodyFont))
        }
        if (report.performanceTrend != null) {
            val diff = report.performanceTrend.currentOverallGe - report.performanceTrend.previousOverallGe
            val arrow = if (diff >= 0) "+" else ""
            summary.add(Chunk("Trend: ", boldFont))
            summary.add(Chunk("$arrow%.1f GE from previous assessment\n".format(diff), bodyFont))
        }
        document.add(summary)
        document.add(Paragraph("\n"))
    }

    private fun addSubjectScoreTable(document: Document, result: AssessmentResult, report: AnalysisReport) {
        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        document.add(Paragraph("Subject Scores", sectionFont))
        document.add(Paragraph("\n"))

        val table = PdfPTable(7)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 1f, 1f, 1.5f, 1.5f, 1f, 0.8f))

        val headerFont = Font(Font.HELVETICA, 10f, Font.BOLD)
        val headers = listOf("Subject", "Scaled Score", "Grade Eq.", "Suggested Level", "Risk Tier", "Correct", "Rank")
        for (header in headers) {
            val cell = PdfPCell(Phrase(header, headerFont))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setPadding(5f)
            cell.backgroundColor = java.awt.Color(230, 230, 230)
            table.addCell(cell)
        }

        val bodyFont = Font(Font.HELVETICA, 10f, Font.NORMAL)
        // Only show core subjects in the main score table; CT/IQ get separate sections
        val coreSubjects = result.subjectResults.filter {
            it.value.scoreBundle.totalCount > 0 && it.key in Subject.CORE_SUBJECTS
        }
        val rankMap = report.subjectRankings.associate { it.subject to it.rank }

        for ((subject, subjectResult) in coreSubjects) {
            val sb = subjectResult.scoreBundle
            val gl = GradeLevel.fromGradeEquivalent(sb.gradeEquivalent)
            table.addCell(PdfPCell(Phrase(subject.displayName, bodyFont)).apply { setPadding(4f) })
            table.addCell(PdfPCell(Phrase("${sb.scaledScore}", bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
            table.addCell(PdfPCell(Phrase("%.1f".format(sb.gradeEquivalent), bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
            table.addCell(PdfPCell(Phrase(gl.display, bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
            table.addCell(PdfPCell(Phrase(sb.riskTier.display, bodyFont)).apply { setPadding(4f) })
            table.addCell(PdfPCell(Phrase("${sb.correctCount}/${sb.totalCount}", bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
            table.addCell(PdfPCell(Phrase("${rankMap[subject] ?: "-"}", bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
        }

        document.add(table)
        document.add(Paragraph("\n"))
    }

    private fun addCriticalThinkingSection(document: Document, result: AssessmentResult) {
        val ctResult = result.subjectResults[Subject.CRITICAL_THINKING] ?: return
        if (ctResult.scoreBundle.totalCount == 0) return

        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        val bodyFont = Font(Font.HELVETICA, 11f, Font.NORMAL)
        val boldFont = Font(Font.HELVETICA, 11f, Font.BOLD)

        document.add(Paragraph("Critical Thinking Assessment", sectionFont))
        document.add(Paragraph("\n"))

        val sb = ctResult.scoreBundle
        val summary = Paragraph()
        summary.add(Chunk("Scaled Score: ", boldFont))
        summary.add(Chunk("${sb.scaledScore}\n", bodyFont))
        summary.add(Chunk("Percentile: ", boldFont))
        summary.add(Chunk("${sb.ctPercentile ?: "N/A"}\n", bodyFont))
        summary.add(Chunk("Correct: ", boldFont))
        summary.add(Chunk("${sb.correctCount}/${sb.totalCount}\n", bodyFont))
        summary.add(Chunk("Theta: ", boldFont))
        summary.add(Chunk("%.2f (SE: %.2f)\n".format(sb.theta, sb.standardError), bodyFont))

        val percentile = sb.ctPercentile ?: 50
        val interpretation = when {
            percentile >= 90 -> "Exceptional critical thinking skills, performing above 90% of age-peers."
            percentile >= 75 -> "Strong critical thinking skills, above average for age group."
            percentile >= 40 -> "Average critical thinking skills for age group."
            percentile >= 20 -> "Below-average critical thinking skills. Structured reasoning practice recommended."
            else -> "Significantly below average. Intensive reasoning skill development recommended."
        }
        summary.add(Chunk("Interpretation: ", boldFont))
        summary.add(Chunk(interpretation + "\n", bodyFont))

        document.add(summary)
        document.add(Paragraph("\n"))
    }

    private fun addIqAssessmentSection(document: Document, result: AssessmentResult) {
        val iqResult = result.subjectResults[Subject.IQ_ASSESSMENT] ?: return
        if (iqResult.scoreBundle.totalCount == 0) return

        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        val bodyFont = Font(Font.HELVETICA, 11f, Font.NORMAL)
        val boldFont = Font(Font.HELVETICA, 11f, Font.BOLD)
        val disclaimerFont = Font(Font.HELVETICA, 10f, Font.BOLD or Font.ITALIC)

        document.add(Paragraph("IQ Assessment", sectionFont))
        document.add(Paragraph("\n"))

        val sb = iqResult.scoreBundle
        val summary = Paragraph()
        summary.add(Chunk("IQ Score: ", boldFont))
        summary.add(Chunk("${sb.iqScore ?: "N/A"}\n", bodyFont))
        summary.add(Chunk("95% Confidence Interval: ", boldFont))
        summary.add(Chunk("${sb.iqRangeLow ?: "N/A"} - ${sb.iqRangeHigh ?: "N/A"}\n", bodyFont))
        summary.add(Chunk("Correct: ", boldFont))
        summary.add(Chunk("${sb.correctCount}/${sb.totalCount}\n", bodyFont))
        summary.add(Chunk("Theta: ", boldFont))
        summary.add(Chunk("%.2f (SE: %.2f)\n".format(sb.theta, sb.standardError), bodyFont))

        val iqScore = sb.iqScore ?: 100
        val classification = when {
            iqScore >= 130 -> "Very Superior"
            iqScore >= 120 -> "Superior"
            iqScore >= 110 -> "High Average"
            iqScore >= 90 -> "Average"
            iqScore >= 80 -> "Low Average"
            iqScore >= 70 -> "Borderline"
            else -> "Extremely Low"
        }
        summary.add(Chunk("Classification: ", boldFont))
        summary.add(Chunk("$classification\n", bodyFont))

        document.add(summary)

        // Mandatory disclaimer
        document.add(Paragraph("\n"))
        document.add(Paragraph(ScoringHelper.IQ_DISCLAIMER, disclaimerFont))
        document.add(Paragraph("\n"))
    }

    private fun addMissedQuestionsSection(document: Document, result: AssessmentResult) {
        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        val subjectFont = Font(Font.HELVETICA, 13f, Font.BOLD)
        val bodyFont = Font(Font.HELVETICA, 10f, Font.NORMAL)
        val smallBold = Font(Font.HELVETICA, 10f, Font.BOLD)
        val noteFont = Font(Font.HELVETICA, 10f, Font.ITALIC)

        var hasMissed = false
        for ((subject, subjectResult) in result.subjectResults) {
            if (subjectResult.missedQuestions.isNotEmpty()) {
                if (!hasMissed) {
                    document.add(Paragraph("Missed Questions", sectionFont))
                    val intro = Paragraph(
                        "This section lists questions the student answered incorrectly. " +
                        "Review these to identify specific skill gaps. The standard code in brackets " +
                        "indicates which curriculum standard the question assesses.",
                        noteFont
                    )
                    document.add(intro)
                    document.add(Paragraph("\n"))
                    hasMissed = true
                }

                document.add(Paragraph(subject.displayName, subjectFont))
                for (missed in subjectResult.missedQuestions) {
                    val p = Paragraph()
                    p.add(Chunk("Q: ", smallBold))
                    p.add(Chunk("${missed.prompt}\n", bodyFont))
                    p.add(Chunk("Student Answer: ", smallBold))
                    p.add(Chunk(missed.studentAnswer.ifBlank { "(no answer)" } + "  ", bodyFont))
                    p.add(Chunk("Correct Answer: ", smallBold))
                    p.add(Chunk(missed.correctAnswer.ifBlank { "(see explanation)" }, bodyFont))
                    if (missed.standardCode.isNotBlank()) {
                        p.add(Chunk("  [${missed.standardCode}]", Font(Font.HELVETICA, 9f, Font.ITALIC)))
                        val desc = standardCodeDescriptions[missed.standardCode]
                        if (desc != null) {
                            p.add(Chunk(" - $desc", Font(Font.HELVETICA, 9f, Font.NORMAL)))
                        }
                    }
                    p.add(Chunk("\n"))
                    document.add(p)
                }
                document.add(Paragraph("\n"))
            }
        }
    }

    private fun addStandardsMasterySection(document: Document, report: AnalysisReport) {
        if (report.standardsMastery.isEmpty()) return

        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        val noteFont = Font(Font.HELVETICA, 10f, Font.ITALIC)
        document.add(Paragraph("Standards Mastery", sectionFont))
        document.add(Paragraph(
            "Standards marked 'Not Mastered' had one or more incorrect responses. " +
            "Focus instruction on these areas to close skill gaps.",
            noteFont
        ))
        document.add(Paragraph("\n"))

        val table = PdfPTable(5)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(1.2f, 3f, 1.5f, 1.2f, 1f))

        val headerFont = Font(Font.HELVETICA, 10f, Font.BOLD)
        for (header in listOf("Standard", "Description", "Subject", "Status", "Errors")) {
            val cell = PdfPCell(Phrase(header, headerFont))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.setPadding(5f)
            cell.backgroundColor = java.awt.Color(230, 230, 230)
            table.addCell(cell)
        }

        val bodyFont = Font(Font.HELVETICA, 10f, Font.NORMAL)
        for (standard in report.standardsMastery) {
            table.addCell(PdfPCell(Phrase(standard.standardCode, bodyFont)).apply { setPadding(4f) })
            val desc = standardCodeDescriptions[standard.standardCode] ?: ""
            table.addCell(PdfPCell(Phrase(desc, bodyFont)).apply { setPadding(4f) })
            table.addCell(PdfPCell(Phrase(standard.subject.displayName, bodyFont)).apply { setPadding(4f) })
            table.addCell(PdfPCell(Phrase(if (standard.mastered) "Mastered" else "Not Mastered", bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
            table.addCell(PdfPCell(Phrase("${standard.totalCount}", bodyFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; setPadding(4f)
            })
        }

        document.add(table)
        document.add(Paragraph("\n"))
    }

    private fun addRecommendationsSection(document: Document, report: AnalysisReport) {
        if (report.recommendations.isEmpty()) return

        val sectionFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        document.add(Paragraph("Recommendations", sectionFont))
        document.add(Paragraph("\n"))

        val bodyFont = Font(Font.HELVETICA, 11f, Font.NORMAL)
        val list = com.lowagie.text.List(com.lowagie.text.List.UNORDERED)
        for (rec in report.recommendations) {
            list.add(ListItem(rec, bodyFont))
        }
        document.add(list)
        document.add(Paragraph("\n"))
    }

    private fun addStandardsCodeReference(document: Document, result: AssessmentResult) {
        // Collect all standard codes used in this assessment
        val usedCodes = mutableSetOf<String>()
        for ((_, subjectResult) in result.subjectResults) {
            for (missed in subjectResult.missedQuestions) {
                if (missed.standardCode.isNotBlank()) {
                    usedCodes.add(missed.standardCode)
                }
            }
        }
        if (usedCodes.isEmpty()) return

        val sectionFont = Font(Font.HELVETICA, 14f, Font.BOLD)
        document.add(Paragraph("Standards Code Reference", sectionFont))
        document.add(Paragraph("\n"))

        val bodyFont = Font(Font.HELVETICA, 9f, Font.NORMAL)
        val boldFont = Font(Font.HELVETICA, 9f, Font.BOLD)

        for (code in usedCodes.sorted()) {
            val desc = standardCodeDescriptions[code]
            val p = Paragraph()
            p.add(Chunk("$code: ", boldFont))
            p.add(Chunk(desc ?: "Curriculum standard", bodyFont))
            p.add(Chunk("\n"))
            document.add(p)
        }
    }

    companion object {
        // Common Core State Standards descriptions used in question banks
        val standardCodeDescriptions = mapOf(
            // Language Arts / Writing standards
            "L.K.2" to "Kindergarten: Capitalize first word, recognize end punctuation, write letters",
            "L.1.1" to "Grade 1: Use common, proper, and possessive nouns; verbs; adjectives",
            "L.1.2" to "Grade 1: Capitalize dates and names, use end punctuation, use commas in dates and lists",
            "L.2.1" to "Grade 2: Use collective nouns, irregular plural nouns, reflexive pronouns, past tense verbs",
            "L.2.2" to "Grade 2: Capitalize holidays, product names, and geographic names; use commas in greetings",
            "L.3.1" to "Grade 3: Explain function of nouns, pronouns, verbs, adjectives, adverbs; form regular/irregular verbs",
            "L.4.1" to "Grade 4: Use relative pronouns, progressive verb tenses, modal auxiliaries, prepositional phrases",
            "L.4.2" to "Grade 4: Use correct capitalization, commas and quotation marks, spell grade-appropriate words",
            "L.5.1" to "Grade 5: Explain function of conjunctions, prepositions, interjections; use verb tenses correctly",
            "L.6.1" to "Grade 6: Ensure pronouns are in proper case, correct vague pronouns, recognize variations in English",
            "L.6.2" to "Grade 6: Use punctuation (commas, parentheses, dashes) to set off nonrestrictive elements",
            "L.7.1" to "Grade 7: Explain function of phrases and clauses, use simple/compound/complex sentences",
            "L.7.2" to "Grade 7: Use commas to separate coordinate adjectives",
            "L.8.1" to "Grade 8: Explain function of verbals (gerunds, participles, infinitives), active/passive voice",
            "L.9-10.1" to "Grades 9-10: Use parallel structure, types of phrases and clauses",

            // Writing standards
            "W.4.3" to "Grade 4: Write narratives with details, dialogue, and clear event sequences",
            "W.7.1" to "Grade 7: Write arguments with claims, reasons, evidence, and counterarguments",
            "W.9-10.1" to "Grades 9-10: Write arguments with substantive claims, reasoning, and rhetorical strategies",
            "W.9-10.2" to "Grades 9-10: Write informative/explanatory texts with thesis, organization, and evidence",
            "W.9-10.8" to "Grades 9-10: Gather and cite sources using standard formats (MLA, APA)",

            // Reading Literature standards
            "RL.1.1" to "Grade 1: Ask and answer questions about key details in a text",
            "RL.2.1" to "Grade 2: Ask and answer who, what, where, when, why, and how questions",
            "RL.3.1" to "Grade 3: Ask and answer questions referring explicitly to the text",
            "RL.4.1" to "Grade 4: Refer to details and examples when explaining what the text says",
            "RL.5.1" to "Grade 5: Quote accurately from a text when explaining inferences",
            "RL.6.1" to "Grade 6: Cite textual evidence to support analysis of what the text says",
            "RL.7.1" to "Grade 7: Cite several pieces of textual evidence to support analysis",
            "RL.8.1" to "Grade 8: Cite the strongest textual evidence to support analysis",

            // Reading Informational standards
            "RI.1.1" to "Grade 1: Ask and answer questions about key details in informational text",
            "RI.2.1" to "Grade 2: Ask and answer questions about key details in informational text",
            "RI.3.1" to "Grade 3: Ask and answer questions referring explicitly to informational text",
            "RI.4.2" to "Grade 4: Determine the main idea and explain how details support it",
            "RI.5.2" to "Grade 5: Determine two or more main ideas and explain how details support them",

            // Reading Foundational skills
            "RF.1.2" to "Grade 1: Demonstrate understanding of spoken words, syllables, and sounds",
            "RF.2.3" to "Grade 2: Know and apply grade-level phonics and word analysis skills",
            "RF.K.3" to "Kindergarten: Know and apply grade-level phonics and word analysis skills",

            // Math - Operations & Algebraic Thinking
            "OA.K.1" to "Kindergarten: Represent addition and subtraction with objects and drawings",
            "OA.1.1" to "Grade 1: Use addition and subtraction within 20 to solve word problems",
            "OA.2.1" to "Grade 2: Use addition and subtraction within 100 to solve word problems",
            "OA.3.1" to "Grade 3: Interpret products of whole numbers",
            "OA.4.1" to "Grade 4: Interpret a multiplication equation as a comparison",

            // Math - Number & Operations in Base Ten
            "NBT.1.1" to "Grade 1: Count to 120; read and write numerals; represent objects with written numerals",
            "NBT.2.1" to "Grade 2: Understand place value (hundreds, tens, ones)",
            "NBT.3.1" to "Grade 3: Use place value understanding to round whole numbers",
            "NBT.4.1" to "Grade 4: Recognize that a digit is 10 times what it represents in the place to its right",
            "NBT.5.1" to "Grade 5: Recognize that a digit is 10 times or 1/10 of what it represents in adjacent places",

            // Math - Number & Operations: Fractions
            "NF.3.1" to "Grade 3: Understand a fraction 1/b as a quantity formed by 1 part of b equal parts",
            "NF.4.1" to "Grade 4: Explain why a fraction a/b is equivalent to (n*a)/(n*b)",
            "NF.5.1" to "Grade 5: Add and subtract fractions with unlike denominators",

            // Math - Geometry
            "G.K.1" to "Kindergarten: Describe objects using names of shapes; describe relative positions",
            "G.1.1" to "Grade 1: Distinguish between defining attributes of shapes",
            "G.3.1" to "Grade 3: Understand shapes may share attributes; categorize shapes by attributes",
            "G.4.1" to "Grade 4: Draw and identify lines, angles, and classify shapes by properties",
            "G.5.1" to "Grade 5: Use a coordinate system; graph points to solve problems",

            // Math - Measurement & Data
            "MD.1.1" to "Grade 1: Order three objects by length; compare lengths of two objects",
            "MD.2.1" to "Grade 2: Measure the length of an object using appropriate tools",
            "MD.3.1" to "Grade 3: Tell and write time to the nearest minute; solve elapsed time problems",

            // Math - Ratios & Proportional Relationships
            "RP.6.1" to "Grade 6: Understand ratio concepts and use ratio reasoning",
            "RP.7.1" to "Grade 7: Compute unit rates associated with ratios of fractions",

            // Math - Expressions & Equations
            "EE.6.1" to "Grade 6: Write and evaluate numerical expressions involving whole-number exponents",
            "EE.7.1" to "Grade 7: Apply properties of operations to add, subtract, factor, and expand linear expressions",
            "EE.8.1" to "Grade 8: Know and apply properties of integer exponents",

            // Math - Functions & Algebra
            "F.8.1" to "Grade 8: Understand that a function assigns each input exactly one output",
            "A.REI.9.1" to "Grades 9-10: Solve linear equations and inequalities in one variable",
            "A.CED.9.1" to "Grades 9-10: Create equations and inequalities in one variable to solve problems",
            "A.SSE.9.1" to "Grades 9-10: Interpret the structure of expressions",

            // Science standards (NGSS-inspired)
            "SCI.K.1" to "Kindergarten: Observe and describe patterns in the natural world",
            "SCI.1.1" to "Grade 1: Plan and conduct investigations to understand cause and effect",
            "SCI.2.1" to "Grade 2: Use observations to construct evidence-based accounts",
            "SCI.3.1" to "Grade 3: Identify evidence supporting scientific claims",
            "SCI.4.1" to "Grade 4: Use models to describe phenomena and test cause-and-effect relationships",
            "SCI.5.1" to "Grade 5: Support arguments with evidence, data, or a model",
            "SCI.6.1" to "Grade 6: Develop and use models to describe phenomena",
            "SCI.7.1" to "Grade 7: Analyze and interpret data to provide evidence for phenomena",
            "SCI.8.1" to "Grade 8: Construct and present arguments supported by evidence",
            "SCI.9.1" to "Grades 9-10: Evaluate claims, evidence, and reasoning in scientific arguments",

            // Social Studies / History
            "SS.K.1" to "Kindergarten: Identify rules and responsibilities in the community",
            "SS.1.1" to "Grade 1: Describe how individuals and events shape the community",
            "SS.2.1" to "Grade 2: Explain how communities change over time",
            "SS.3.1" to "Grade 3: Use map skills and geographic terms to describe locations",
            "SS.4.1" to "Grade 4: Explain how government structures work at state and local levels",
            "SS.5.1" to "Grade 5: Describe causes and effects of major historical events in the U.S.",
            "SS.6.1" to "Grade 6: Analyze primary and secondary sources to understand ancient civilizations",
            "SS.7.1" to "Grade 7: Evaluate the impact of cultural diffusion and trade networks",
            "SS.8.1" to "Grade 8: Analyze the causes and consequences of key events in world history",
            "SS.9.1" to "Grades 9-10: Evaluate multiple perspectives and bias in historical sources",

            // Arts
            "ART.K.1" to "Kindergarten: Explore and identify basic elements of art (color, shape, line)",
            "ART.1.1" to "Grade 1: Use art vocabulary to describe artwork and creative choices",
            "ART.2.1" to "Grade 2: Identify and compare uses of art in various cultures",
            "ART.3.1" to "Grade 3: Apply knowledge of elements and principles of design to create artwork",
            "ART.4.1" to "Grade 4: Analyze how art reflects and influences culture and history",
            "ART.5.1" to "Grade 5: Evaluate artwork using criteria such as composition, technique, and meaning",
            "ART.6.1" to "Grade 6: Analyze relationships between visual arts and other disciplines",
            "ART.7.1" to "Grade 7: Interpret meaning and purpose of artwork in historical and cultural contexts",
            "ART.8.1" to "Grade 8: Critique artwork using formal analysis and contextual understanding",
            "ART.9.1" to "Grades 9-10: Synthesize knowledge of art history, theory, and criticism",

            // Critical Thinking standards
            "CT.K.1" to "Kindergarten: Basic inference from simple observations",
            "CT.K.2" to "Kindergarten: Recognize simple patterns and sequences",
            "CT.K.3" to "Kindergarten: Basic deductive reasoning with concrete objects",
            "CT.K.4" to "Kindergarten: Identify simple cause and effect",
            "CT.K.5" to "Kindergarten: Pattern logic with shapes and colors",
            "CT.3.1" to "Grade 3: Identify assumptions in everyday statements",
            "CT.3.2" to "Grade 3: Apply deductive reasoning to draw conclusions",
            "CT.3.3" to "Grade 3: Recognize patterns and predict outcomes",
            "CT.3.4" to "Grade 3: Evaluate simple arguments for strength",
            "CT.3.5" to "Grade 3: Inductive reasoning from observations",
            "CT.6.1" to "Grade 6: Distinguish correlation from causation",
            "CT.6.2" to "Grade 6: Identify hidden assumptions in arguments",
            "CT.6.3" to "Grade 6: Apply necessary and sufficient conditions",
            "CT.6.4" to "Grade 6: Evaluate competing arguments for validity",
            "CT.6.5" to "Grade 6: Inductive reasoning with complex data",
            "CT.9.1" to "Grade 9: Identify logical fallacies (ad hominem, false dilemma, etc.)",
            "CT.9.2" to "Grade 9: Analyze complex arguments for validity and soundness",
            "CT.9.3" to "Grade 9: Advanced inference from complex scenarios",
            "CT.9.4" to "Grade 9: Evaluate rhetorical strategies and hidden premises",
            "CT.9.5" to "Grade 9: Distinguish valid from sound arguments",

            // IQ Assessment standards
            "IQ.K.1" to "Kindergarten: Simple pattern recognition with shapes and colors",
            "IQ.K.2" to "Kindergarten: Basic sequence completion",
            "IQ.K.3" to "Kindergarten: Simple analogies with concrete objects",
            "IQ.K.4" to "Kindergarten: Basic logical deduction",
            "IQ.K.5" to "Kindergarten: Verbal reasoning (categorization)",
            "IQ.3.1" to "Grade 3: Number sequence completion",
            "IQ.3.2" to "Grade 3: Analogies with abstract concepts",
            "IQ.3.3" to "Grade 3: Verbal reasoning (odd-one-out)",
            "IQ.3.4" to "Grade 3: Transitive logical reasoning",
            "IQ.3.5" to "Grade 3: Spatial reasoning (paper folding)",
            "IQ.6.1" to "Grade 6: Complex number sequences",
            "IQ.6.2" to "Grade 6: Abstract analogies",
            "IQ.6.3" to "Grade 6: Spatial reasoning (3D visualization)",
            "IQ.6.4" to "Grade 6: Quantified syllogistic reasoning",
            "IQ.6.5" to "Grade 6: Advanced verbal classification",
            "IQ.9.1" to "Grade 9: Advanced abstract pattern recognition",
            "IQ.9.2" to "Grade 9: Complex number sequence analysis",
            "IQ.9.3" to "Grade 9: Higher-order analogical reasoning",
            "IQ.9.4" to "Grade 9: Contrapositive and conditional reasoning",
            "IQ.9.5" to "Grade 9: Abstract matrix pattern completion"
        )
    }
}
