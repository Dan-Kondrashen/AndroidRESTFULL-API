package ru.kondrashin.myworkingproject

import java.util.Date
import java.util.UUID

data class Comment (
    var id: Int,
    var content: String,
    var comment_date: String,
    var authorId: Int,
    var blogId: Int
)