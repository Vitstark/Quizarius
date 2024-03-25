package ru.itis.security.service

import io.smallrye.jwt.build.Jwt
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.json.JsonNumber
import jakarta.ws.rs.NotFoundException
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.Claims
import org.eclipse.microprofile.jwt.JsonWebToken
import ru.itis.quizarius.dao.UserDao
import ru.itis.quizarius.dto.User
import ru.itis.security.encryption.Encryptor
import java.time.Duration

/**
 * @author Vitaly Chekushkin
 */

@ApplicationScoped
class JwtSecurityService(
    private val userDao: UserDao,
    private val encryptor: Encryptor,
    private val jwt: JsonWebToken,
    @ConfigProperty(name = "mp.jwt.verify.issuer") val issuer: String
) : SecurityService {

    override fun authenticate(login: String, password: CharSequence): Uni<String> = userDao
        .getUser(login, encryptor.encrypt(password))
        .onItem().ifNull().failWith { NotFoundException("User with this credentials not found") }
        .map { toJwtToken(it) }


    override fun getAuthenticatedUser() = jwt.claim<JsonNumber>("userId")
        .map { id -> id.longValue() }
        .map { id -> User(id, jwt.claim<String>(Claims.preferred_username).get()) }
        .orElseThrow { NotFoundException("Jwt not available") }

    private fun toJwtToken(user: User): String = Jwt
        .issuer(issuer)
        .claim("userId", user.id)
        .claim(Claims.preferred_username, user.username)
        .expiresIn(Duration.ofHours(9999))
        .sign()
}