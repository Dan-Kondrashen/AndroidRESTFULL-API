package ru.kondrashin.myworkingproject

import android.content.Context
import java.util.UUID

class AuthorLab private constructor(context: Context){
    var authors = mutableListOf<Author>()
    companion object {
        private var INSTANCE: AuthorLab? = null
        fun get(context: Context): AuthorLab {
            if (INSTANCE == null)
                INSTANCE = AuthorLab(context)
            return INSTANCE!!
        }

    }

    fun getAuthor(id: Int): Author? {
        for (author in authors) {
            if (author.id == id) {
                return author
            }
        }
        return null
    }

    fun addAuthor(author: Author){
        authors.add(author)
    }
}