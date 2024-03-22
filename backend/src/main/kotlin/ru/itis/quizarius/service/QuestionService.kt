package ru.itis.quizarius.service

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.SecurityContext
import org.jooq.quizarius.enums.Category
import ru.itis.quizarius.dao.QuestionDao
import ru.itis.quizarius.dao.StatisticDao
import ru.itis.quizarius.dto.QuestionResponseDto

/**
 * @author Vitaly Chekushkin
 */

@ApplicationScoped
class QuestionService(val questionDao: QuestionDao, val statisticDao: StatisticDao) {

    var securityContext: SecurityContext? = null

    fun getQuestionByCategory(category: Category): Uni<QuestionResponseDto> =
        questionDao.getRandomQuestionIdByCategory(category)
            .flatMap { questionId ->
                Uni.combine().all().unis(
                    questionDao.getQuestionById(questionId!!),
                    questionDao.getAnswersByQuestionId(questionId).collect().asList(),
                    statisticDao.recordGiveQuestion(questionId, null)
                ).asTuple()
            }
            .map { tuple -> QuestionResponseDto(tuple.item1, tuple.item2, tuple.item3) }
}