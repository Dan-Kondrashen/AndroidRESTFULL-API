package ru.kondrashin.myworkingproject

import java.util.Date
import java.util.UUID

data class Author (
    var id: Int,
    var firstname: String,
    var lastname: String,
    var phone: Long,
    var email: String,
    var registration_date: Date
)