package ru.itis.security.encryption

import jakarta.enterprise.context.ApplicationScoped


@ApplicationScoped
class NoOpEncryptor : Encryptor {
    override fun encrypt(sequence: CharSequence): String {
        return sequence.toString()
    }
}