package ru.itis.quizarius.dao

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import jakarta.inject.Singleton
import org.jooq.DSLContext
import org.jooq.impl.DSL.asterisk
import org.jooq.impl.DSL.rand
import org.jooq.quizarius.enums.Category
import org.jooq.quizarius.tables.Answers
import org.jooq.quizarius.tables.references.ANSWERS
import org.jooq.quizarius.tables.references.QUESTIONS
import ru.itis.extensions.fetch
import ru.itis.extensions.fetchOne
import ru.itis.extensions.get
import ru.itis.quizarius.dto.Answer
import ru.itis.quizarius.dto.Question

/**
 * @author Vitaly Chekushkin
 */

@Singleton
class QuestionDao(private val client: Pool, private val dsl: DSLContext) {

    private val questionMapper: (Row) -> Question = { row ->
        Question(
            row.get(QUESTIONS.ID)!!,
            row.get(QUESTIONS.BODY)!!
        )
    }

    private val answerMapper: (Row) -> Answer = { row ->
        Answer(
            row.get(ANSWERS.ID)!!,
            row.get(ANSWERS.BODY)!!
        )
    }

    fun getQuestionById(id: Long) =
        dsl.select(asterisk())
            .from(QUESTIONS)
            .where(QUESTIONS.ID.eq(id))
            .fetchOne(client)
            .map(questionMapper)

    fun getRandomQuestionIdByCategory(category: Category) =
        dsl.select(QUESTIONS.ID)
            .from(QUESTIONS)
            .where(QUESTIONS.CATEGORY.eq(category))
            .orderBy(rand())
            .limit(1)
            .fetchOne(client, QUESTIONS.ID)

    fun getAnswersByQuestionId(questionId: Long) =
        dsl.select(asterisk())
            .from(ANSWERS)
            .where(ANSWERS.QUESTION_ID.eq(questionId))
            .and(ANSWERS.IS_CORRECT.isTrue)
            .union(
                dsl.select(asterisk())
                    .from(ANSWERS)
                    .where(ANSWERS.QUESTION_ID.eq(questionId))
                    .and(ANSWERS.IS_CORRECT.isFalse)
                    .orderBy(rand())
                    .limit(3)
            )
            .orderBy(rand())
            .fetch(client)
            .map(answerMapper)

    fun isCorrect(questionId: Long, answerId: Long) = dsl
        .select(ANSWERS.IS_CORRECT)
        .from(ANSWERS)
        .where(ANSWERS.QUESTION_ID.eq(questionId).and(ANSWERS.ID.eq(answerId)))
        .fetchOne(client)
        .map { row -> row.get(ANSWERS.IS_CORRECT) }
}