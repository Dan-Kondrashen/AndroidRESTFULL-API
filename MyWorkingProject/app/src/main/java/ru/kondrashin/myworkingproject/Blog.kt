package ru.kondrashin.myworkingproject

import java.util.Date
import java.util.UUID

data class Blog (
    val id:Int,
    var title: String,
    var date: String,
    var content: String,
    var authorId: Int
)

