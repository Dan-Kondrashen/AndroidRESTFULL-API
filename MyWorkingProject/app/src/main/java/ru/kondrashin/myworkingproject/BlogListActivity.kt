package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

class BlogListActivity: SingleFragmentActivity() {
    companion object{
        private const val EXTRA_AUTHOR_ID = "ru.kondrashin.myworkingproject.author_id"
        fun newIntent(packageContext: Context?, authorId: Int): Intent? {
            val intent = Intent(packageContext, BlogListActivity::class.java)
            intent.putExtra(EXTRA_AUTHOR_ID, authorId)
            return intent
        }
    }
    override fun createFragment(): BlogListFragment{
        val authorId = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as Int
        return BlogListFragment.newInstance(authorId)

    }
    fun restartActivity(blogListActivity: BlogListActivity){
        blogListActivity.recreate()
    }
}