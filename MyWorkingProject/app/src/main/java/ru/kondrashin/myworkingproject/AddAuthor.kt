package ru.kondrashin.myworkingproject

data class AddAuthor(
    var firstname: String,
    var lastname: String,
    var phone: Long,
    var email: String,
    var password: String
)