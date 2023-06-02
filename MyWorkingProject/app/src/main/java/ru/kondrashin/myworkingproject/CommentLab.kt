package ru.kondrashin.myworkingproject

import android.content.Context
import java.util.UUID

class CommentLab private constructor(context: Context) {
    var comments = mutableListOf<Comment>()
    companion object {
        private var INSTANCE: CommentLab? = null
        fun get(context: Context): CommentLab {
            if (INSTANCE == null)
                INSTANCE = CommentLab(context)
            return INSTANCE!!
        }

    }
    fun getComment(id: Int): Comment? {
        for (comment in comments) {
            if (comment.id == id) {
                return comment
            }
        }
        return null
    }

}