package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NewBlogActivity: SingleFragmentActivity() {
    companion object {
        private const val EXTRA_AUTHOR_ID = "ru.rsue.android.myworkingproject.author_id"

        fun newIntent(packageContext: Context?, authorId: Int): Intent? {
            val intent = Intent(packageContext, NewBlogActivity::class.java)
            intent.putExtra(EXTRA_AUTHOR_ID, authorId)
            return intent
        }
    }
    override fun createFragment() : NewBlogFragment{
        val authorId = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as Int
        return NewBlogFragment.newInstance(authorId)
    }
}