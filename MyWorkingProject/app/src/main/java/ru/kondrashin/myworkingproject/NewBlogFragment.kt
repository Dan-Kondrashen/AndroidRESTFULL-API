package ru.kondrashin.myworkingproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.kondrashin.myworkingproject.api.BlogsApi
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.CoroutineContext

class NewBlogFragment: Fragment(), CoroutineScope {
    private lateinit var titleField: EditText
    private lateinit var contentField: EditText
    private lateinit var addButton: Button
    private lateinit var backButton: Button
    private var idAuth = 0
    companion object {
        private const val ARG_AUTHOR_ID = "author_id"



        fun newInstance(authorId: Int) = NewBlogFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_AUTHOR_ID, authorId)
            }

        }
    }

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, BlogFragment.DateTypeAdapter())
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://e109-85-249-173-247.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        .build()
    val blogsApi = retrofit.create(BlogsApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as Int
        idAuth = authorId
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.add_blog_fragment, container, false)
        titleField = v.findViewById(R.id.blog_title)
        contentField = v.findViewById(R.id.blog_content)
        addButton = v.findViewById(R.id.add_blog)
        addButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                val blog = blogsApi.postBlogAsync(
                    AddBlog(
                        titleField.text.toString(),
                        contentField.text.toString(),
                        idAuth
                    )
                )
            }
            val intent = BlogListActivity.newIntent(requireActivity(), idAuth)
            requireActivity().finish()
            startActivity(intent)
        }
        backButton = v.findViewById(R.id.go_on_main)
        backButton.setOnClickListener {
            val intent = BlogListActivity.newIntent(requireActivity(), idAuth)
            requireActivity().finish()
            startActivity(intent)
        }

        return v
    }
}
