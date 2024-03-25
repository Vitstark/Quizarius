package ru.itis.security.service

import io.smallrye.mutiny.Uni
import ru.itis.quizarius.dto.User

/**
 * @author Vitaly Chekushkin
 */
interface SecurityService {
    fun authenticate(login: String, password: CharSequence): Uni<String>
    fun getAuthenticatedUser(): User?
}