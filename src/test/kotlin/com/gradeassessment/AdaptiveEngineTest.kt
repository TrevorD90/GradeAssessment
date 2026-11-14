package com.gradeassessment

import com.gradeassessment.data.questionbank.QuestionBankLoader
import com.gradeassessment.data.questionbank.QuestionRepository
import com.gradeassessment.domain.agent.AdaptiveQuestionAgent
import com.gradeassessment.domain.agent.IrtEngine
import com.gradeassessment.domain.model.*
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdaptiveEngineTest {

    private val repository = QuestionRepository().also { it.loadAll() }

    @Test
    fun `grade scale round trips and matches the bank calibration`() {
        assertEquals(0.0, GradeScale.geToTheta(5.0), 1e-9)
        assertEquals(3.5, GradeScale.thetaToGe(GradeScale.geToTheta(3.5)), 1e-9)
        assertEquals(100, GradeScale.thetaToScaledScore(GradeScale.geToTheta(0.0)))
        assertEquals(GradeLevel.K, GradeLevel.fromGradeEquivalent(0.9))
        assertEquals(GradeLevel.G3, GradeLevel.fromGradeEquivalent(3.0))
        assertEquals(GradeLevel.G10, GradeLevel.fromGradeEquivalent(10.9))
    }

    @Test
    fun `one answer never throws the estimate to the end of the scale`() {
        val start = AdaptiveState.fromAge(5) // kindergartner
        val item = IrtParameters(a = 1.2, b = start.currentTheta, c = 0.25)
        val (thetaRight, _) = IrtEngine.estimate(IrtEngine.absorbResponse(null, item, true), start.priorMean)
        val (thetaWrong, _) = IrtEngine.estimate(IrtEngine.absorbResponse(null, item, false), start.priorMean)
        // The old maximum-likelihood estimator returned +3.0 (10th grade) / -3.0 here.
        assertTrue(GradeScale.thetaToGe(thetaRight) < 2.5, "one correct answer moved a 5-year-old to GE ${GradeScale.thetaToGe(thetaRight)}")
        assertTrue(thetaWrong < thetaRight)
        assertTrue(thetaWrong > GradeScale.THETA_MIN + 0.1)
    }

    @Test
    fun `first question is pitched about one grade below the age expectation`() {
        val state = AdaptiveState.fromAge(9) // grade 4, expected GE 4.5
        assertEquals(3.5, GradeScale.thetaToGe(state.currentTheta), 1e-9)
        val agent = AdaptiveQuestionAgent(repository, Random(1))
        val first = assertNotNull(agent.selectNextQuestion(state, Subject.MATH))
        assertTrue(first.gradeLevel in 2..4, "first math question for a 9-year-old was grade ${first.gradeLevel}")
    }

    @Test
    fun `selection never repeats a question and alternates format when it can`() {
        val agent = AdaptiveQuestionAgent(repository, Random(7))
        var state = AdaptiveState.fromAge(10)
        val seen = HashSet<String>()
        var sameTypeInARow = 0
        var previous: Question? = null
        repeat(30) { i ->
            val q = assertNotNull(agent.selectNextQuestion(state, Subject.SCIENCE))
            assertTrue(seen.add(q.id), "question ${q.id} repeated")
            if (previous?.type == q.type) sameTypeInARow++
            previous = q
            state = agent.updateState(state, q, response(q, correct = i % 2 == 0, state = state, index = i))
        }
        assertTrue(sameTypeInARow <= 6, "same question type back-to-back $sameTypeInARow times")
    }

    /**
     * End-to-end check of "does the test pinpoint the grade level": simulated students with a known
     * true level answer according to the 3PL model, in every subject, and the reported grade
     * equivalent must land close to the truth.
     */
    @Test
    fun `simulated students are placed near their true grade level in every subject`() {
        val trueLevels = listOf(0.5, 2.5, 4.5, 6.5, 8.5, 10.2)
        for (subject in Subject.entries) {
            for (trueGe in trueLevels) {
                val errors = ArrayList<Double>()
                val lengths = ArrayList<Int>()
                repeat(40) { run ->
                    val random = Random(subject.ordinal * 1000 + (trueGe * 10).toInt() * 50 + run)
                    val agent = AdaptiveQuestionAgent(repository, random)
                    // The student's age is deliberately 2 years off their true level, so the test
                    // has to move away from its starting guess.
                    val age = (trueGe.toInt() + 5 + if (run % 2 == 0) 2 else -2).coerceIn(5, 16)
                    var state = AdaptiveState.fromAge(age)
                    val trueTheta = GradeScale.geToTheta(trueGe)
                    while (!agent.shouldTerminate(state)) {
                        val q = agent.selectNextQuestion(state, subject) ?: break
                        val correct = random.nextDouble() < IrtEngine.probability3PL(trueTheta, q.irtParameters)
                        state = agent.updateState(state, q, response(q, correct, state, state.questionCount))
                    }
                    errors.add(abs(GradeScale.thetaToGe(state.currentTheta) - trueGe))
                    lengths.add(state.questionCount)
                }
                val meanError = errors.average()
                println("SIM %-17s trueGE=%4.1f meanAbsError=%.2f grades, avgQuestions=%.1f".format(subject, trueGe, meanError, lengths.average()))
                assertTrue(
                    meanError < 0.9,
                    "$subject true GE $trueGe: mean error ${"%.2f".format(meanError)} grades over ${lengths.average()} questions"
                )
                assertTrue(lengths.all { it in IrtEngine.MIN_QUESTIONS..IrtEngine.MAX_QUESTIONS })
            }
        }
    }

    /**
     * Test-wiseness guard: a 10-year-old who knows none of the content and answers multiple-choice
     * items by always choosing the choice at one fixed length position (longest, 2nd-longest, ...)
     * must not be placed meaningfully higher than one who guesses at random. Typed / ordering items
     * cannot be guessed this way, so the simulated child gets them wrong.
     */
    @Test
    fun `choosing answers by length does not inflate the result`() {
        for (subject in Subject.entries) {
            fun placement(pick: (Question, Random) -> String?): Double {
                val results = (0 until 30).map { run ->
                    val random = Random(subject.ordinal * 7919 + run)
                    val agent = AdaptiveQuestionAgent(repository, random)
                    var state = AdaptiveState.fromAge(10)
                    while (!agent.shouldTerminate(state)) {
                        val q = agent.selectNextQuestion(state, subject) ?: break
                        val correct = pick(q, random) == q.correctAnswer &&
                            q.type in setOf(QuestionType.MULTIPLE_CHOICE, QuestionType.PASSAGE_BASED)
                        state = agent.updateState(state, q, response(q, correct, state, state.questionCount))
                    }
                    GradeScale.thetaToGe(state.currentTheta)
                }
                return results.average()
            }
            val randomGe = placement { q, r -> q.choices.randomOrNull(r) }
            for (rank in 0..2) {
                val lengthGe = placement { q, _ ->
                    q.choices.sortedByDescending { it.length }.getOrNull(rank)
                }
                println("GUESS %-17s random=%.1f lengthRank%d=%.1f".format(subject, randomGe, rank, lengthGe))
                assertTrue(
                    lengthGe - randomGe < 1.0,
                    "$subject: picking the length-rank-$rank choice places a know-nothing child at GE " +
                        "%.1f vs %.1f for random guessing".format(lengthGe, randomGe)
                )
            }
        }
    }

    @Test
    fun `question bank loads cleanly with enough questions at every grade and difficulty`() {
        repository.loadAll()
        assertEquals(emptyList(), QuestionBankLoader.lastLoadProblems)
        for (subject in Subject.entries) {
            val pool = repository.getAdaptivePool(subject)
            // Sorted by difficulty: required by the binary-search selector.
            assertTrue(pool.zipWithNext().all { (a, b) -> a.irtParameters.b <= b.irtParameters.b })
            for (grade in 0..10) {
                val atGrade = pool.filter { it.gradeLevel == grade }
                assertTrue(atGrade.size >= 12, "$subject grade $grade has only ${atGrade.size} questions")
                for (difficulty in listOf("EASY", "MEDIUM", "HARD")) {
                    assertTrue(
                        atGrade.count { it.difficulty == difficulty } >= 3,
                        "$subject grade $grade has too few $difficulty questions"
                    )
                }
            }
            // Pool must be several times the maximum test length.
            assertTrue(pool.size >= 4 * IrtEngine.MAX_QUESTIONS, "$subject pool is only ${pool.size}")
        }
        for (subtest in listOf(ReadingSubtest.LETTER_NAMING, ReadingSubtest.PHONEME_SEGMENTATION, ReadingSubtest.NONSENSE_WORD)) {
            assertTrue(repository.getReadingFluencyQuestions(subtest).isNotEmpty(), "no $subtest forms")
        }
    }

    @Test
    fun `answers are scored fairly`() {
        val fill = question(QuestionType.FILL_IN_BLANK).copy(correctAnswer = "32", correctAnswers = listOf("thirty-two"))
        assertTrue(AnswerEvaluator.isCorrect(fill, " 32 "))
        assertTrue(AnswerEvaluator.isCorrect(fill, "32.0"))
        assertTrue(AnswerEvaluator.isCorrect(fill, "Thirty-Two."))
        assertFalse(AnswerEvaluator.isCorrect(fill, "23"))
        val small = question(QuestionType.FILL_IN_BLANK).copy(correctAnswer = "4")
        assertTrue(AnswerEvaluator.isCorrect(small, "four"))
        assertTrue(AnswerEvaluator.isCorrect(small, "Four."))
        val negative = question(QuestionType.FILL_IN_BLANK).copy(correctAnswer = "-3")
        assertTrue(AnswerEvaluator.isCorrect(negative, "−3")) // typographic minus sign
        val nile = question(QuestionType.FILL_IN_BLANK).copy(subject = Subject.WORLD_HISTORY, correctAnswer = "Nile")
        assertTrue(AnswerEvaluator.isCorrect(nile, "the Nile"))
        val article = question(QuestionType.FILL_IN_BLANK).copy(subject = Subject.WRITING, correctAnswer = "an apple")
        assertFalse(AnswerEvaluator.isCorrect(article, "a apple")) // in Writing the article is the skill
        val cloze = question(QuestionType.CLOZE).copy(prompt = "2 + 2 = ___", blanks = listOf("4"))
        assertTrue(AnswerEvaluator.isCorrect(cloze, "", answers = listOf("four")))
        assertFalse(AnswerEvaluator.isCorrect(fill, ""))

        val multi = question(QuestionType.MULTI_SELECT).copy(choices = listOf("a", "b", "c", "d"), correctAnswers = listOf("a", "c"))
        assertTrue(AnswerEvaluator.isCorrect(multi, "", answers = listOf("c", "a")))
        assertFalse(AnswerEvaluator.isCorrect(multi, "", answers = listOf("a")))
        assertFalse(AnswerEvaluator.isCorrect(multi, "", answers = emptyList()))

        val ordering = question(QuestionType.ORDERING).copy(orderedItems = listOf("1", "2", "3"))
        assertTrue(AnswerEvaluator.isCorrect(ordering, "", orderedItems = listOf("1", "2", "3")))
        assertFalse(AnswerEvaluator.isCorrect(ordering, "", orderedItems = listOf("2", "1", "3")))

        val fluency = question(QuestionType.TIMED_FLUENCY).copy(
            fluencyItems = listOf("cat", "dog", "sun", "map", "hen"),
            fluencyCorrectItems = setOf("/k/ /a/ /t/")
        )
        assertTrue(AnswerEvaluator.isCorrect(fluency, "", fluencySelections = setOf("cat", "dog", "sun")))
        assertFalse(AnswerEvaluator.isCorrect(fluency, "", fluencySelections = setOf("cat", "dog")))

        // Defective items are rejected by the loader instead of being served.
        assertNotNull(question(QuestionType.MULTI_SELECT).copy(choices = listOf("a", "b")).structuralDefect())
        assertNotNull(question(QuestionType.ORDERING).structuralDefect())
    }

    @Test
    fun `critical thinking and IQ are scored relative to age`() {
        // A 6-year-old reasoning exactly at the level expected for age 6 is average, not "IQ 60".
        val theta = GradeScale.geToTheta(GradeScale.expectedGeForAge(6))
        val iq = IrtEngine.computeIqScoreBundle(theta, 0.3, emptyList(), age = 6)
        assertEquals(100, iq.iqScore)
        val ct = IrtEngine.computeCtScoreBundle(theta, 0.3, emptyList(), age = 6)
        assertTrue(ct.ctPercentile in 49..51)
    }

    private fun question(type: QuestionType) = Question(
        id = "t", subject = Subject.MATH, type = type, gradeRange = "3-5", prompt = "p"
    )

    private fun response(q: Question, correct: Boolean, state: AdaptiveState, index: Int) = StudentResponse(
        questionId = q.id, subject = q.subject, subtest = q.readingSubtest, studentAnswer = "",
        correct = correct, timeToAnswerMs = 1000, thetaAtTime = state.currentTheta, questionIndex = index
    )
}
