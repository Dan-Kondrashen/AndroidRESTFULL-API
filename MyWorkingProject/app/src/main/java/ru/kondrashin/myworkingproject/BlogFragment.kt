package ru.kondrashin.myworkingproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kondrashin.myworkingproject.api.AuthorsApi
import ru.kondrashin.myworkingproject.api.BlogApi
import ru.kondrashin.myworkingproject.api.CommentsApi
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext

class BlogFragment : Fragment(), CoroutineScope {
    companion object {
        private const val ARG_BLOG_ID = "blog_id"
        private const val ARG_AUTHOR_ID = "author_id"
        const val REQUEST_DATE = "REQUEST_DATE"
        private val DIALOG_DATE = "DialogDate"
        private val CREATE_COMM = "CreateComm"
        private const val TAG = "CommentsForBlog"
        private const val LOAD_DATA = "loadDataFinished"


        fun newInstance(blogId: Int, authorId: Int) = BlogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_BLOG_ID, blogId)
                putSerializable(ARG_AUTHOR_ID, authorId)
            }

        }
    }
    private var commentRecyclerView: RecyclerView? = null
    private var adapter: CommentAdapter? = null
    private var blog: Blog? = null
    private var authors: Author? = null
    private var authorss: List<Author>? = null
    private lateinit var authorNickname: TextView
    private lateinit var titleField: EditText
    private lateinit var contentField: EditText
    private lateinit var dateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var updateButton: Button
    private lateinit var addCommButton: Button
    private lateinit var backButton: Button

    private var loadDataFinished = false
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://e109-85-249-173-247.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))

        .build()

    val commentApi = retrofit.create(CommentsApi::class.java)
    val blogApi = retrofit.create(BlogApi::class.java)
    val authorApi = retrofit.create(AuthorsApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super. onCreate(savedInstanceState)
        val blogId = requireArguments().getSerializable(ARG_BLOG_ID) as Int
        val idAuth = requireArguments().getSerializable(ARG_AUTHOR_ID) as Int
        blog = BlogLab.get(requireActivity()).getBlog(blogId)
        authors = AuthorLab.get(requireActivity()).getAuthor(idAuth)
        val authorLab = AuthorLab.get(requireContext())
        authorss = authorLab.authors


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null){
            loadDataFinished = savedInstanceState.getBoolean(BlogFragment.LOAD_DATA, false)
        }
        //  Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_blog, container, false)

        authorNickname = v.findViewById(R.id.blog_author_view)
        for (author in authorss!!)
            if(author.id == blog!!.authorId)
                authorNickname.text = "Автор: " + author.firstname + " " + author.lastname
        titleField = v.findViewById(R.id.blog_title)
        titleField.setText(blog?.title)
        titleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь намеренно оставлено пустое место
            }
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                blog?.title = s.toString()
            }
            override fun afterTextChanged(c: Editable) {
                // И здесь тоже
            }
        })
        contentField = v.findViewById(R.id.blog_content)
        contentField.setText(blog?.content)
        contentField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь намеренно оставлено пустое место
            }
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                blog?.content = s.toString()
            }
            override fun afterTextChanged(c: Editable) {
                // И здесь тоже
            }
        })
        dateButton = v.findViewById(R.id.blog_date)
        dateButton.text = blog?.date
//        dateButton.setOnClickListener {
//            val manager = parentFragmentManager
//            val dialog = DatePickerFragment.newInstance(blog?.date)
//            manager.setFragmentResultListener(REQUEST_DATE, this) {
//                    requestKey, bundle ->
////                 T/O/D/O
//                val selectedDate = bundle.getSerializable(DatePickerFragment.EXTRA_DATE)
//                        as Date
//                blog?.date = selectedDate
//                dateButton.text = selectedDate.toString()
//
//            }
//
//            dialog.show(manager, DIALOG_DATE)
//        }
        updateButton = v.findViewById(R.id.update_button)
        updateButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val blog = blogApi.putBlogAsync(
                    blog!!.id,
                    Blog(
                        blog!!.id,
                        titleField.text.toString(),
                        dateButton.text.toString(),
                        contentField.text.toString(),
                        authors!!.id

                    )
                )
            }
            val intent = BlogListActivity.newIntent(requireActivity(), authors!!.id)
            startActivity(intent)
        }
        deleteButton = v.findViewById(R.id.delete_button)
        deleteButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val blog = blogApi.deleteBlogAsync(blog!!.id)
            }
            val intent = BlogListActivity.newIntent(requireActivity(), authors!!.id)
            startActivity(intent)
        }
        if (blog!!.authorId == authors!!.id) {
            titleField.isFocusableInTouchMode = true
            contentField.isFocusableInTouchMode = true
            deleteButton.visibility = View.VISIBLE
            updateButton.visibility = View.VISIBLE
        }
        else {
            titleField.isFocusable = false
            contentField.isFocusable = false
            deleteButton.visibility = View.INVISIBLE
            updateButton.visibility = View.INVISIBLE
        }
        addCommButton = v.findViewById(R.id.add_com)
        addCommButton.setOnClickListener {
            val manager = parentFragmentManager
            val dialog2 = NewCommentFragment.newInstance(authors!!.id, blog!!.id)

            dialog2.show(manager, CREATE_COMM)


        }
        backButton = v.findViewById(R.id.go_on_main)
        backButton.setOnClickListener {
            val intent = BlogListActivity.newIntent(requireActivity(), authors!!.id)
            requireActivity().finish()
            startActivity(intent)

        }
        commentRecyclerView = v.findViewById(R.id.comment_recycler_view)
        commentRecyclerView!!.layoutManager = LinearLayoutManager(activity)

        if (loadDataFinished)
            updateUI(authors!!.id)
        else
            loadData()
        return v
    }
    override fun onResume() {
        super.onResume()
        updateUI(authors!!.id)
    }
//    override fun onDestroy() {
//        super.onDestroy()
//        coroutineContext.cancelChildren()
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BlogFragment.LOAD_DATA,loadDataFinished)
    }
    private fun updateUI(authorId: Int) {

        val commentLab = CommentLab.get(requireActivity())
        val authorLab = AuthorLab.get(requireActivity())


        val comments = commentLab.comments
        val authors = authorLab.authors
        if (adapter == null) {
            adapter = CommentAdapter(comments, authors, authorId)
            commentRecyclerView!!.adapter = adapter

        }
        else
            adapter!!.notifyItemChanged(commentRecyclerView!!.id )


    }
    private fun loadData() = runBlocking { // (1)
        val job = launch { // (2)

            val commentLab = CommentLab.get(requireActivity())
            val authorLab = AuthorLab.get(requireActivity())
            val postRequest = commentApi.getCommentsAsync(blog!!.id)
            val authRequest = authorApi.getAuthorsAsync()

            try {
                commentLab.comments = postRequest as MutableList<Comment>
                authorLab.authors = authRequest as MutableList<Author>
            } catch (e: Exception) {
                Log.d(BlogFragment.TAG, e.message.toString())
            } finally {
                updateUI(authors!!.id)
            }
        }
        job.join()
        loadDataFinished = true
    }
    class DateTypeAdapter : JsonDeserializer<Date?> {
        @SuppressLint("SimpleDateFormat")
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type?,
                                 context: JsonDeserializationContext?
        ): Date? {
            val date = json.asString
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return try {
                formatter.parse(date)
            } catch (e: ParseException) {
                Log.d("ApiFactory", e.message.toString())
                return null
            }
        }
    }
    private class CommentHolder(itemView: View?, authorId: Int) : RecyclerView.ViewHolder(itemView!!),CoroutineScope, View.OnClickListener {

        private var idAuth = authorId
        private val job = SupervisorJob()
        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Main
        private val gsonBuilder = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        private var retrofit = Retrofit.Builder()
            .baseUrl("https://e109-85-249-173-247.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))

            .build()

        val commentApi = retrofit.create(CommentsApi::class.java)
        //        var titleTextView: TextView? = itemView as TextView?
        private var authorTextView: TextView =
            itemView!!.findViewById(R.id.list_item_comment_author_text_view)
        private var contentTextView: TextView = itemView!!.findViewById(R.id.list_item_comment_content_text_view)
        private var dateTextView: TextView = itemView!!.findViewById(R.id.list_item_comment_date_text_view)
        private var deleteButton: ImageView = itemView!!.findViewById(R.id.delete_comm)
        private var updateButton: ImageView = itemView!!.findViewById(R.id.edit_comm)




        private lateinit var comment: Comment

        fun bindComment(comm: Comment, authors: List<Author>) {
            this.comment = comm
            contentTextView.text = comm.content
            dateTextView.setText(comm.comment_date.toString())

            for (author in authors)
                if(comm.authorId == author.id)
                    authorTextView.text = author.firstname + " " + author.lastname
            if (idAuth == comm.authorId) {
                deleteButton.visibility = View.VISIBLE
                updateButton.visibility = View.VISIBLE
            }
            else {
                deleteButton.visibility = View.INVISIBLE
                updateButton.visibility = View.INVISIBLE
            }
            deleteButton.setOnClickListener{
                CoroutineScope(Dispatchers.IO).launch {
                    if (idAuth == comm.authorId) {
                        commentApi.deleteCommAsync(comm.id)
                        val context = it.context
                        val intent = BlogPagerActivity.newIntent(context, comm.blogId, idAuth)
                        context.startActivity(intent)
                    }


                }

            }
            updateButton.setOnClickListener{
                CoroutineScope(Dispatchers.IO).launch {
                    if (idAuth == comm.authorId) {
                        val context = it.context
                        val intent = CommentActivity.newIntent(context, comm.id,comm.blogId, idAuth)
                        context.startActivity(intent)

                    }
                }
            }

            itemView.setOnClickListener(this)

        }


        override fun onClick(v: View?) {
            //            Toast.makeText(v!!.context,
            //                "${book.title} Выбрана!", Toast.LENGTH_SHORT)
            //                .show()
//            val context = v!!.context
//            //
//            val intent = BlogPagerActivity.newIntent(context, comment.id)
//            context.startActivity(intent)


        }
    }
    private class CommentAdapter(comments: List<Comment>?, authors: List<Author>, authorId: Int) : RecyclerView.Adapter<CommentHolder?>() {
        private var comments: List<Comment>? = null
        private var authors: List<Author>? = null
        private var authorId: Int = 0


        init {
            this.comments = comments
            this.authors = authors
            this.authorId = authorId


        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_comment,
                parent, false)
            return CommentHolder(view, authorId)
        }
        override fun onBindViewHolder(holder: CommentHolder, position: Int) {

            val comment = comments!![position]
//            holder.titleTextView!!.text = book.title
            holder.bindComment(comment, authors!!)
            holder.adapterPosition


//            holder.adapterPosition


        }
        override fun getItemCount() = comments?.size ?: 0
//        override fun getItemCount(): Int {
//            return books!!.size
//        }
    }

}
