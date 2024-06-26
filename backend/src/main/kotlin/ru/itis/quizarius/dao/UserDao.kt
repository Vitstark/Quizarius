package ru.itis.quizarius.dao

import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import jakarta.inject.Singleton
import org.jooq.DSLContext
import org.jooq.impl.DSL.asterisk
import org.jooq.quizarius.tables.references.USER_ACCOUNT
import ru.itis.extensions.fetchOne
import ru.itis.extensions.get
import ru.itis.quizarius.dto.User

/**
 * @author Vitaly Chekushkin
 */

@Singleton
class UserDao(private val pool: Pool, private val dsl: DSLContext) {

    private val mapper: (Row) -> User = { row: Row ->
        User(
            row.get(USER_ACCOUNT.ID)!!,
            row.get(USER_ACCOUNT.EMAIL)!!
        )
    }

    fun getUser(id: Long) = dsl
        .select(asterisk())
        .from(USER_ACCOUNT)
        .where(USER_ACCOUNT.ID.eq(id))
        .fetchOne(pool)
        .map(mapper)

    fun getUser(email: String, password: String) = dsl
        .select(asterisk())
        .from(USER_ACCOUNT)
        .where(USER_ACCOUNT.EMAIL.eq(email).and(USER_ACCOUNT.PASSWORD.eq(password)))
        .fetchOne(pool)
        .map(mapper)
}
