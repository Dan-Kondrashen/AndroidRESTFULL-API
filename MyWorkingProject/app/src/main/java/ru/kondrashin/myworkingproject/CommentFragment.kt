package ru.kondrashin.myworkingproject

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kondrashin.myworkingproject.api.CommentsApi
import java.util.Date
import kotlin.coroutines.CoroutineContext

class CommentFragment : Fragment(), CoroutineScope {
    private lateinit var contentField: EditText
    private lateinit var t: TextView
    private lateinit var updateButton: Button
    private lateinit var backButton: Button
    private var idComm = 0
    private var idAuth = 0
    private var idBlog = 0
    companion object {
        private const val ARG_BLOG_ID = "blog_id"
        private const val ARG_AUTHOR_ID = "author_id"
        private const val ARG_COMM_ID = "comm_id"

        private const val LOAD_DATA = "loadDataFinished"


        fun newInstance(commentId: Int, blogId: Int, authorId: Int) = CommentFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_BLOG_ID, blogId)
                putSerializable(ARG_AUTHOR_ID, authorId)
                putSerializable(ARG_COMM_ID, commentId)
            }

        }
    }
    private var comment: Comment? = null
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, BlogFragment.DateTypeAdapter())
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://e109-85-249-173-247.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))

        .build()

    val commentApi = retrofit.create(CommentsApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commentId = requireArguments().getSerializable(ARG_COMM_ID) as Int
        val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as Int
        val blogId = requireArguments().getSerializable(ARG_BLOG_ID) as Int
        idComm = commentId
        idAuth = authorId
        idBlog = blogId
        comment = CommentLab.get(requireActivity()).getComment(idComm)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_comment, container, false)
        contentField = v.findViewById(R.id.content_comm)
        contentField.setText(comment?.content)
        contentField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь намеренно оставлено пустое место
            }
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                comment?.content = s.toString()
            }
            override fun afterTextChanged(c: Editable) {
                // И здесь тоже
            }
        })

        t = v.findViewById(R.id.server_request)
        updateButton = v.findViewById(R.id.update_button)
        updateButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val comment = commentApi.putCommAsync(
                    comment!!.id,
                    Comment(
                        comment!!.id,
                        contentField.text.toString(),
                        comment!!.comment_date,
                        comment!!.authorId,
                        comment!!.blogId,
                    )
                )
                apply {
                    t.text= comment.status
                    if (t.text =="Успешно") {
                        val intent = BlogPagerActivity.newIntent(requireActivity(), idBlog, idAuth)
                        requireActivity().finish()
                        startActivity(intent)
                        t.setTextColor(Color.GREEN)
                    }
                    else{
                        t.setTextColor(Color.RED)
                    }


                }
            }
        }

        backButton = v.findViewById(R.id.go_on_main)
        backButton.setOnClickListener {
            val intent = BlogPagerActivity.newIntent(requireActivity(), idBlog,idAuth)

            requireActivity().finish()
            startActivity(intent)
        }
        return v
    }
}