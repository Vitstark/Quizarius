package ru.itis.security.encryption

/**
 * @author Vitaly Chekushkin
 */
interface Encryptor {
    fun encrypt(sequence: CharSequence): String
}