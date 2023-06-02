package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent

class BlogActivity : SingleFragmentActivity() {
    companion object {
        private const val EXTRA_BLOG_ID = "ru.kondrashin.myworkingproject.blog_id"
        private const val EXTRA_AUTHOR_ID = "ru.kondrashin.myworkingproject.author_id"
        fun newIntent(packageContext: Context?, blogId: Int, authorId: Int): Intent? {
            val intent = Intent(packageContext, BlogActivity::class.java)
            intent.putExtra(EXTRA_BLOG_ID, blogId)
            intent.putExtra(EXTRA_AUTHOR_ID, authorId)
            return intent
        }
    }

    override fun createFragment() : BlogFragment{
        val blogId = intent.getSerializableExtra(EXTRA_BLOG_ID) as Int
        val authorId = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as Int
        return BlogFragment.newInstance(blogId, authorId)
    }
    fun restartActivity(blogActivity: BlogActivity){
        blogActivity.recreate()
    }
}