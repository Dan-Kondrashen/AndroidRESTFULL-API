package ru.kondrashin.myworkingproject

import android.content.Context

class BlogLab private constructor(context: Context){
    var blogs = mutableListOf<Blog>()
    companion object {
        private var INSTANCE: BlogLab? = null
        fun get(context: Context): BlogLab {
            if (INSTANCE == null)
                INSTANCE = BlogLab(context)
            return INSTANCE!!
        }

    }
    fun getBlog(id: Int): Blog? {
        for (blog in blogs) {
            if (blog.id == id) {
                return blog
            }
        }
        return null
    }
    fun addBlog(blog: Blog){
        blogs.add(blog)
    }


//    init {
//        for (i in 0..99) {
//            val blog = Blog()
//            blog.title = "Блог №$i"
//            blogs.add(blog)
//        }
//    }
}