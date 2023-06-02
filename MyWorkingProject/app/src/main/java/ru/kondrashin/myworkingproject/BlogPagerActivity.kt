package ru.kondrashin.myworkingproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import java.util.UUID

class BlogPagerActivity : AppCompatActivity(){
    private lateinit var viewPager: ViewPager2
    private lateinit var blogs: List<Blog>

    companion object {
        private const val EXTRA_BLOG_ID = "ru.kondrashin.myworkingproject.blog_id"
        private const val EXTRA_AUTHOR_ID = "ru.kondrashin.myworkingproject.author_id"

        fun newIntent(packageContext: Context?, blogId: Int, authorId: Int) = Intent( packageContext,
            BlogPagerActivity::class.java
        ).apply {
            putExtra(EXTRA_BLOG_ID, blogId)
            putExtra(EXTRA_AUTHOR_ID, authorId)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_pager)
        val blogId = intent.getSerializableExtra(EXTRA_BLOG_ID) as Int
        val idAuth = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as Int
        viewPager = findViewById(R.id.activity_blog_pager_view_pager)
        viewPager.adapter = ViewPagerAdapter(this, idAuth)


        blogs = BlogLab.get(this).blogs
        for (i in blogs.indices)
            if (blogs[i].id == blogId) {
                viewPager.currentItem = i
                break
            }


    }

    private class ViewPagerAdapter(fragmentActivity: FragmentActivity, authorId: Int) : FragmentStateAdapter(fragmentActivity) {
        private val blogs: List<Blog> =
            BlogLab.get(fragmentActivity).blogs
        private var idAuth = authorId
        override fun getItemCount() = blogs.size
        override fun createFragment(position: Int) = BlogFragment.newInstance(blogs[position].id, idAuth)
    }

}