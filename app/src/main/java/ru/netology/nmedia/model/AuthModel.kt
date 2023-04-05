package ru.netology.nmedia.model

data class AuthModel(
    val id: Long,
    val token: String
)

data class UserAuthResult (
    val error: Boolean = false
)