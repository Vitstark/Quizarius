package ru.itis.extensions

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.RowSet
import io.vertx.mutiny.sqlclient.Tuple
import org.jooq.Field
import org.jooq.Query
import org.jooq.Record
import org.jooq.Select
import org.jooq.conf.ParamType

/**
 * @author Vitaly Chekushkin
 */

// jOOQ extensions

fun <R: Record?, T> Select<R>.fetchOne(pool: Pool, field: Field<T>): Uni<T> {
    return this.fetchOne(pool)
        .map { row -> row.get(field) }
}

fun <R : Record?> Select<R>.fetchOne(pool: Pool): Uni<Row> {
    return pool.executePreparedQuery(this)
        .map { rs ->
            if (rs.iterator().hasNext())
                rs.iterator().next()
            else
                null
        }
}

fun <R : Record?> Select<R>.fetch(pool: Pool): Multi<Row> {
    return pool.executePreparedQuery(this)
        .onItem()
        .transformToMulti(RowSet<Row>::toMulti)
}

// jOOQ select query extensions

fun <T> Query.execute(pool: Pool, field: Field<T>): Uni<T>? {
    return pool.executePreparedQuery(this)
        .map { rs ->
            if (rs.iterator().hasNext())
                rs.iterator().next().get(field)
            else
                null
        }
}

// jOOQ params extensions

fun <R : Record?> Select<R>.getParamsTuple(): Tuple {
    return this.bindValues
        .let { Tuple.from(it) }
}

fun Query.getParamsTuple(): Tuple {
    return this.params
        .map { it.value.value }
        .let { Tuple.from(it) }
}

// Reactive SQL client extensions

fun <R : Record?> Pool.executePreparedQuery(select: Select<R>): Uni<RowSet<Row>> {
    return this.preparedQuery(select.getSQL(ParamType.NAMED)
        .replace(":", "$"))
        .execute(select.getParamsTuple())
}

fun Pool.executePreparedQuery(query: Query): Uni<RowSet<Row>> {
    return this.preparedQuery(query.getSQL(ParamType.NAMED)
        .replace(":", "$"))
        .execute(query.getParamsTuple())
}

fun <T> Row.get(field: Field<T>): T {
    return this.get(
        field.type,
        field.unqualifiedName.unquotedName().toString().lowercase()
    )
}