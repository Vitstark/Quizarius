package ru.itis.quizarius.dao

import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import jakarta.inject.Singleton
import org.jooq.*
import org.jooq.quizarius.enums.AnswerStatus
import org.jooq.quizarius.tables.records.StatisticRecord
import org.jooq.quizarius.tables.references.STATISTIC
import ru.itis.extensions.execute
import ru.itis.extensions.fetchOne
import ru.itis.extensions.get
import java.time.LocalDateTime

/**
 * @author Vitaly Chekushkin
 */

@Singleton
class StatisticDao(val pool: PgPool, val dsl: DSLContext) {

    private val mapper: (Row) -> StatisticRecord = { row ->
        StatisticRecord(
            row.get(STATISTIC.ID),
            row.get(STATISTIC.STATUS),
            row.get(STATISTIC.DATETIME),
            row.get(STATISTIC.QUESTION_ID),
            row.get(STATISTIC.ANSWER_ID),
            row.get(STATISTIC.USER_ACCOUNT_ID)
        )
    }

    fun getBy(statisticId: Long, userAccountId: Long) = dsl
        .selectFrom(STATISTIC)
        .where(STATISTIC.ID.eq(statisticId))
        .and(STATISTIC.USER_ACCOUNT_ID.eq(userAccountId))
        .fetchOne(pool)
        .map(mapper)

    fun recordGiveQuestion(questionId: Long, userId: Long?) = dsl
        .insertInto(STATISTIC, STATISTIC.QUESTION_ID, STATISTIC.USER_ACCOUNT_ID, STATISTIC.DATETIME)
        .values(questionId, userId, LocalDateTime.now())
        .returning(STATISTIC.ID)
        .execute(pool, STATISTIC.ID)

    fun setStatusById(id: Long, answerStatus: AnswerStatus) = dsl
        .update(STATISTIC)
        .set(STATISTIC.STATUS, answerStatus)
        .where(STATISTIC.ID.eq(id))
        .returning(STATISTIC.STATUS)
        .execute(pool, STATISTIC.STATUS)
}