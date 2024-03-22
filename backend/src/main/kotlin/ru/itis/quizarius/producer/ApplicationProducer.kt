package ru.itis.quizarius.producer

import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.PoolOptions
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.*
import org.jooq.impl.DefaultConfiguration

/**
 * @author Vitaly Chekushkin
 */

@ApplicationScoped
class ApplicationProducer(
    @ConfigProperty(name = "quarkus.datasource.reactive.url") val url: String,
    @ConfigProperty(name = "quarkus.datasource.username") val username: String,
    @ConfigProperty(name = "quarkus.datasource.password") val password: String
) {

    @Produces
    fun pool(): Pool {
        val conOptions = PgConnectOptions.fromUri(url)
        conOptions.setUser(username)
        conOptions.setPassword(password)
        return PgPool.pool(Vertx.vertx(), conOptions, PoolOptions())
    }

    @Produces
    fun dsl(): DSLContext {
        val configuration = DefaultConfiguration()
            .set(SQLDialect.POSTGRES)
            .dsl()
        configuration.settings().renderNameCase = RenderNameCase.LOWER
        configuration.settings().renderQuotedNames = RenderQuotedNames.NEVER
        configuration.settings().renderKeywordCase = RenderKeywordCase.LOWER
        return configuration
    }

}