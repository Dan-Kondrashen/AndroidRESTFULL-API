package ru.kondrashin.myworkingproject

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kondrashin.myworkingproject.api.CommentsApi
import java.util.Date
import java.util.GregorianCalendar
import kotlin.coroutines.CoroutineContext

class NewCommentFragment: DialogFragment(), CoroutineScope {

    private lateinit var contentField: EditText
    private lateinit var addButton: Button
    private var idAuth = 0
    private var idBlog = 0
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
    companion object {
        private const val ARG_AUTHOR_ID = "author_id"
        private const val ARG_BLOG_ID = "blog_id"

        fun newInstance(authorId: Int,blogId: Int) = NewCommentFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_AUTHOR_ID, authorId)
                putSerializable(ARG_BLOG_ID, blogId)
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super. onCreate(savedInstanceState)
        val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as Int
        idAuth = authorId
        val blogId = requireArguments().getSerializable(ARG_BLOG_ID) as Int
        idBlog = blogId

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = layoutInflater.inflate(R.layout.add_comment_fragment, null)
        contentField = v.findViewById(R.id.comment_text)
        addButton = v.findViewById(R.id.add_comm)
        addButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val blog = commentApi.postCommentAsync(
                    idBlog,
                    AddComment(
                        contentField.text.toString(),
                        idAuth,
                        idBlog
                    )
                )
            }

            val intent = BlogPagerActivity.newIntent(requireActivity(),idBlog ,idAuth )
            requireActivity().finish()
            startActivity(intent)

        }
        return AlertDialog.Builder(requireActivity())
            .setView(v)
            .setTitle(R.string.add_comm)
            .create()
    }

}
