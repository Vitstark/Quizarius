package ru.itis.quizarius.dto

/**
 * @author Vitaly Chekushkin
 */
data class QuestionResponseDto(
    var question: Question? = null,
    var answers: List<Answer>? = null,
    var statisticId: Long? = null
)