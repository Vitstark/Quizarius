package ru.itis.quizarius.dao

import io.vertx.mutiny.pgclient.PgPool
import jakarta.inject.Singleton
import org.jooq.DSLContext
import org.jooq.quizarius.tables.references.STATISTIC
import ru.itis.extensions.execute

/**
 * @author Vitaly Chekushkin
 */

@Singleton
class StatisticDao(val pool: PgPool, val dsl: DSLContext) {

    fun recordGiveQuestion(questionId: Long, userId: Long?) = dsl
        .insertInto(STATISTIC, STATISTIC.QUESTION_ID, STATISTIC.USER_ACCOUNT_ID)
        .values(questionId, userId)
        .returning(STATISTIC.ID)
        .execute(pool, STATISTIC.ID)
}