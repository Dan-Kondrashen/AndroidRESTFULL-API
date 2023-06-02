package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent

class CommentActivity: SingleFragmentActivity() {
    companion object {
        private const val EXTRA_COMM_ID = "ru.kondrashin.myworkingproject.comm_id"
        private const val EXTRA_BLOG_ID = "ru.kondrashin.myworkingproject.blog_id"
        private const val EXTRA_AUTHOR_ID = "ru.kondrashin.myworkingproject.author_id"
        fun newIntent(packageContext: Context?,commId: Int, blogId: Int, authorId: Int): Intent? {
            val intent = Intent(packageContext, CommentActivity::class.java)
            intent.putExtra(EXTRA_COMM_ID, commId)
            intent.putExtra(EXTRA_BLOG_ID, blogId)
            intent.putExtra(EXTRA_AUTHOR_ID, authorId)
            return intent
        }
    }

    override fun createFragment() : CommentFragment{
        val blogId = intent.getSerializableExtra(EXTRA_BLOG_ID) as Int
        val authorId = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as Int
        val commentId = intent.getSerializableExtra(EXTRA_COMM_ID) as Int
        return CommentFragment.newInstance(commentId, blogId, authorId)
    }
}