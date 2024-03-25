package ru.itis.quizarius.rest

import io.quarkus.security.Authenticated
import jakarta.ws.rs.ApplicationPath

/**
 * @author Vitaly Chekushkin
 */

@ApplicationPath("/statistic")
@Authenticated
class StatisticResource {

}