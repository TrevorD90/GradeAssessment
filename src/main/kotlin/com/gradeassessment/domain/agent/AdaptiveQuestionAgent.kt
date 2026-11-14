package com.gradeassessment.domain.agent

import com.gradeassessment.data.questionbank.QuestionRepository
import com.gradeassessment.domain.model.*
import kotlin.random.Random

class AdaptiveQuestionAgent(
    private val questionRepository: QuestionRepository,
    private val random: Random = Random.Default
) {

    /** Next adaptive question for [subject] (for Reading this is the comprehension pool). */
    fun selectNextQuestion(state: AdaptiveState, subject: Subject): Question? =
        IrtEngine.selectNextQuestion(
            theta = state.currentTheta,
            sortedByDifficulty = questionRepository.getAdaptivePool(subject),
            askedIds = state.askedIds,
            lastBloomsLevel = state.lastBloomsLevel,
            lastQuestionType = state.lastQuestionType,
            random = random
        )

    /** A random unseen form of the given fluency subtest (previously always the first form). */
    fun selectFluencyQuestion(subtest: ReadingSubtest, state: AdaptiveState): Question? =
        questionRepository.getReadingFluencyQuestions(subtest)
            .filter { it.id !in state.askedIds }
            .randomOrNull(random)

    fun selectReadingComprehensionQuestion(state: AdaptiveState): Question? =
        selectNextQuestion(state, Subject.READING)

    /**
     * Folds one scored answer into the ability estimate. O(G) regardless of how many questions
     * have been answered (the running likelihood is cached in the state).
     */
    fun updateState(state: AdaptiveState, question: Question, response: StudentResponse): AdaptiveState {
        val logLikelihood = IrtEngine.absorbResponse(
            state.posteriorLogLikelihood, question.irtParameters, response.correct
        )
        val (newTheta, se) = IrtEngine.estimate(logLikelihood, state.priorMean)

        return state.copy(
            currentTheta = newTheta,
            standardError = se,
            responses = state.responses + response,
            questionsAsked = state.questionsAsked + question.id,
            askedIds = state.askedIds + question.id,
            posteriorLogLikelihood = logLikelihood,
            lastBloomsLevel = question.bloomsLevel,
            lastQuestionType = question.type
        )
    }

    fun shouldTerminate(state: AdaptiveState): Boolean = IrtEngine.shouldTerminate(state)
}
