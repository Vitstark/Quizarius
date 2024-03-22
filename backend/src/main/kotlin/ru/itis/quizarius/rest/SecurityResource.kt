package ru.itis.quizarius.rest

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import ru.itis.quizarius.dao.UserDao

/**
 * @author Vitaly Chekushkin
 */

@Path("/security/identity")
class SecurityResource(val userDao: UserDao) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun identity() = userDao.getUserById(1)

}
