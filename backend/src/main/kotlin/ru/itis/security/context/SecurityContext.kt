package ru.itis.security.context

import jakarta.enterprise.context.RequestScoped
import jakarta.json.JsonNumber
import org.eclipse.microprofile.jwt.JsonWebToken

/**
 * @author Vitaly Chekushkin
 */

@RequestScoped
class SecurityContext(val token: JsonWebToken) {
    fun getCurrentUserId() = token.claim<JsonNumber>("userId")
        .map { id -> id.longValue() }
        .orElseThrow()
}