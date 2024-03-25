package ru.itis.quizarius.service

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.quizarius.enums.AnswerStatus
import org.jooq.quizarius.enums.Category
import ru.itis.quizarius.dao.QuestionDao
import ru.itis.quizarius.dao.StatisticDao
import ru.itis.quizarius.dto.AnswerOnQuestion
import ru.itis.quizarius.dto.QuestionResponseDto
import ru.itis.security.context.SecurityContext

/**
 * @author Vitaly Chekushkin
 */

@ApplicationScoped
class QuestionService(
    val questionDao: QuestionDao,
    val statisticDao: StatisticDao,
    val securityContext: SecurityContext
) {

    fun getQuestionByCategory(category: Category): Uni<QuestionResponseDto> =
        questionDao.getRandomQuestionIdByCategory(category)
            .flatMap { questionId ->
                Uni.combine().all().unis(
                    questionDao.getQuestionById(questionId!!),
                    questionDao.getAnswersByQuestionId(questionId).collect().asList(),
                    statisticDao.recordGiveQuestion(questionId, securityContext.getCurrentUserId())
                ).asTuple()
            }
            .map { tuple -> QuestionResponseDto(tuple.item1, tuple.item2, tuple.item3) }

    fun answerOnQuestion(answerOnQuestion: AnswerOnQuestion) = statisticDao
        .getBy(answerOnQuestion.statisticId, securityContext.getCurrentUserId())
        .onItem().ifNull().failWith { NotFoundException("Not found statistic answer") }
        .flatMap { statisticRecord ->
            questionDao.isCorrect(
                statisticRecord.questionId!!,
                answerOnQuestion.answerId
            )
        }
        .flatMap { isCorrect ->
            statisticDao.setStatusById(
                answerOnQuestion.statisticId,
                if (isCorrect == true) AnswerStatus.CORRECT else AnswerStatus.WRONG
            )
        }
}