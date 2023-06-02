package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent

class CurAuthorActivity: SingleFragmentActivity()  {
    companion object {
        private const val EXTRA_AUTHOR_ID = "ru.kondrashin.myworkingproject.author_id"

        fun newIntent(packageContext: Context?, authorId: Int): Intent? {
            val intent = Intent(packageContext, CurAuthorActivity::class.java)
            intent.putExtra(EXTRA_AUTHOR_ID, authorId)
            return intent
        }
    }
    override fun createFragment() : CurAuthorFragment{
        val authorId = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as Int
        return CurAuthorFragment.newInstance(authorId)
    }
}