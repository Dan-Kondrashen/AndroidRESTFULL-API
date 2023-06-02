package ru.kondrashin.myworkingproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kondrashin.myworkingproject.api.ApiFactory
import ru.kondrashin.myworkingproject.api.AuthorsApi
import ru.kondrashin.myworkingproject.api.BlogsApi
import kotlin.coroutines.CoroutineContext

class BlogListFragment: Fragment(), CoroutineScope {
    private var blogRecyclerView: RecyclerView? = null
    private var adapter: BlogAdapter? = null
    private lateinit var progressBar: ProgressBar
    private var loadDataFinished = false
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://e109-85-249-173-247.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create())

        .build()

    val blogApi = retrofit.create(BlogsApi::class.java)
    val authorApi = retrofit.create(AuthorsApi::class.java)
    private var idAuth = 0

    companion object {

        private const val LOAD_DATA = "loadDataFinished"
        private const val TAG = "BlogListFragment"
        private const val ARG_AUTHOR_ID = "author_id"

        fun newInstance(authorId: Int) = BlogListFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_AUTHOR_ID, authorId)
            }

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as Int
        idAuth = authorId


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        if (savedInstanceState != null){
            loadDataFinished = savedInstanceState.getBoolean(LOAD_DATA, false)
        }
        val view: View = inflater.inflate(
            R.layout.fragment_blog_list, container,
            false
        )
        blogRecyclerView = view.findViewById(R.id.blog_recycler_view)
        blogRecyclerView!!.layoutManager = LinearLayoutManager(activity)

        progressBar = view.findViewById(R.id.progressBar)
        if (loadDataFinished)
            updateUI()
        else
            loadData()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(LOAD_DATA,loadDataFinished)
    }
    override fun onResume() {
        super.onResume()
        updateUI()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_blog_list_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_item_new_blog -> {
            val intent = NewBlogActivity.newIntent(requireActivity(),idAuth)
            requireActivity().finish()
            startActivity(intent)
            true
        }
        R.id.menu_item_user_info -> {
            val intent = CurAuthorActivity.newIntent(requireActivity(),idAuth)
            requireActivity().finish()
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }




//    override fun onDestroy() {
//        super.onDestroy()
//        coroutineContext.cancelChildren()
//    }


    private fun updateUI() {

        val blogLab = BlogLab.get(requireActivity())
        val authorLab = AuthorLab.get(requireContext())
        val authors = authorLab.authors
        val blogs = blogLab.blogs
        if (adapter == null) {
            adapter = BlogAdapter(blogs, authors, idAuth)
            blogRecyclerView!!.adapter = adapter

        }
        else

            adapter!!.notifyItemChanged(blogRecyclerView!!.id )

    }
    private fun loadData() = runBlocking { // (1)
        val job = launch { // (2)
            progressBar.visibility = View.VISIBLE
            val bookLab = BlogLab.get(requireActivity())
            val authorLab = AuthorLab.get(requireActivity())
            val authRequest = authorApi.getAuthorsAsync()
            val postRequest = blogApi.getBlogsAsync()

            try {
                bookLab.blogs = postRequest as MutableList<Blog>
                authorLab.authors = authRequest as  MutableList<Author>
                Log.d("listFirst", bookLab.blogs[1].toString())
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            } finally {
                updateUI()
                progressBar.visibility = View.GONE
            }
        }
        job.join()
        loadDataFinished = true
    }
    private class BlogHolder(itemView: View?, authorId: Int) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {

        //        var titleTextView: TextView? = itemView as TextView?
        private var titleTextView: TextView =
            itemView!!.findViewById(R.id.list_item_blog_title_text_view)
        private var dateTextView: TextView = itemView!!.findViewById(R.id.list_item_blog_date_text_view)
        private var authorTextView: TextView = itemView!!.findViewById(R.id.list_item_blog_author_text_view)
        private var idAuth = authorId



        private lateinit var blog: Blog

        fun bindBlog(blog: Blog, authors: List<Author>) {
            this.blog = blog
            titleTextView.text = blog.title
            dateTextView.setText(blog.date.toString())
            for (author in authors)
                if(blog.authorId == author.id)
                    authorTextView.text = "Автор: " + author.firstname + " " + author.lastname

            itemView.setOnClickListener(this)

        }


        override fun onClick(v: View?) {
            //            Toast.makeText(v!!.context,
            //                "${book.title} Выбрана!", Toast.LENGTH_SHORT)
            //                .show()
            val context = v!!.context
            //
            val intent = BlogPagerActivity.newIntent(context, blog.id, idAuth)
            context.startActivity(intent)

        }
    }
    private class BlogAdapter(blogs: List<Blog>?, authors: List<Author>, authorId: Int) : RecyclerView.Adapter<BlogHolder?>() {
        private var blogs: List<Blog>? = null
        private  var authors: List<Author>? = null
        private  var idAuth = authorId

        init {
            this.blogs = blogs
            this.authors = authors
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_blog,
                parent, false)
            return BlogHolder(view, idAuth)
        }
        override fun onBindViewHolder(holder: BlogHolder, position: Int) {

            val blog = blogs!![position]

//            holder.titleTextView!!.text = book.title
            holder.bindBlog(blog, authors!!)
            holder.adapterPosition


//            holder.adapterPosition


        }
        override fun getItemCount() = blogs?.size ?: 0
//        override fun getItemCount(): Int {
//            return books!!.size
//        }
    }
}