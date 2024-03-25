package ru.itis.quizarius.rest

import io.quarkus.security.Authenticated
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.PermitAll
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import ru.itis.security.dto.Credentials
import ru.itis.security.service.SecurityService

/**
 * @author Vitaly Chekushkin
 */

@Path("/security")
class SecurityResource(
    val securityService: SecurityService
) {

    @GET
    @Path("/identity")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    fun identity() = securityService.getAuthenticatedUser();

    @POST
    @Path("/authenticate")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    fun authenticate(credentials: Credentials): Uni<String> = securityService
        .authenticate(credentials.login, credentials.password)
}
