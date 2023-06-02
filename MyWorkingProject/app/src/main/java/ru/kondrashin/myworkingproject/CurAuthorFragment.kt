package ru.kondrashin.myworkingproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import ru.kondrashin.myworkingproject.api.AuthorsApi
import java.util.Date
import kotlin.coroutines.CoroutineContext

class CurAuthorFragment : Fragment(), CoroutineScope{
    private lateinit var firstnameField: EditText
    private lateinit var lastnameField: EditText
    private lateinit var emailField: EditText
    private lateinit var t: TextView
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button

    private var loadDataFinished = false
    private var idAuth = 0
    companion object {
        private const val ARG_AUTHOR_ID = "author_id"
        private const val LOAD_DATA = "loadDataFinished"



        fun newInstance(authorId: Int) = CurAuthorFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_AUTHOR_ID, authorId)
            }

        }
    }
    private var authors: Author? = null

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, BlogFragment.DateTypeAdapter())
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://e109-85-249-173-247.ngrok-free.app/")
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        .build()
    val authorApi = retrofit.create(AuthorsApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as Int
        idAuth = authorId
        authors = AuthorLab.get(requireActivity()).getAuthor(idAuth)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.author_logout_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_item_logout -> {
            val intent = RegOrLogActivity.newIntent(requireActivity())
            requireActivity().finish()
            startActivity(intent)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_author, container, false)
        firstnameField = v.findViewById(R.id.author_firstname)
        firstnameField.setText(authors?.firstname)
        firstnameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь намеренно оставлено пустое место
            }
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                authors?.firstname = s.toString()
            }
            override fun afterTextChanged(c: Editable) {
                // И здесь тоже
            }
        })
        lastnameField = v.findViewById(R.id.author_lastname)
        lastnameField.setText(authors?.lastname)
        lastnameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь намеренно оставлено пустое место
            }
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                authors?.lastname = s.toString()
            }
            override fun afterTextChanged(c: Editable) {
                // И здесь тоже
            }
        })
        emailField = v.findViewById(R.id.author_email)
        emailField.setText(authors?.email)
        emailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь намеренно оставлено пустое место
            }
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                authors?.email = s.toString()
            }
            override fun afterTextChanged(c: Editable) {
                // И здесь тоже
            }
        })
        t = v.findViewById(R.id.server_request)
        updateButton = v.findViewById(R.id.update_button)
        updateButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val author = authorApi.putAuthorAsync(
                    authors!!.id,
                    UpdateAuthor(
                        authors!!.id,
                        firstnameField.text.toString(),
                        lastnameField.text.toString(),
                        emailField.text.toString()
                    )
                )
                apply {
                    t.text= author.status


                }
            }
        }
        deleteButton = v.findViewById(R.id.delete_button)
        deleteButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val author = authorApi.deleteAuthorAsync(authors!!.id)
            }
            val intent = RegOrLogActivity.newIntent(requireActivity())
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
