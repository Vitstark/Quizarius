package ru.itis.quizarius.rest

import io.quarkus.security.Authenticated
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.Path

/**
 * @author Vitaly Chekushkin
 */

@Path("/statistic")
@Authenticated
class StatisticResource {

}