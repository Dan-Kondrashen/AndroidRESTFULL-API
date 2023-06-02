package ru.kondrashin.myworkingproject

import android.os.Bundle
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
import ru.kondrashin.myworkingproject.api.AuthorsApi
import java.util.Date
import kotlin.coroutines.CoroutineContext

class LoginFragment : Fragment(), CoroutineScope {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var logButton: Button
    private lateinit var t: TextView

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_login, container, false)
        email = v.findViewById(R.id.et_email)
        password = v.findViewById(R.id.et_password)
        t = v.findViewById(R.id.ViewForLogin)
        logButton = v.findViewById(R.id.btn_login)
        logButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {

                val author = authorApi.logAuthorAsync(
                    LogAuthor(
                        email.text.toString(),
                        password.text.toString()
                    )
                )
                apply {
                    t.text= author.status
                    val a = "Вы успешно авторизованы!"
                    if (author.status==a) {
                        val intent = BlogListActivity.newIntent(requireActivity(),author.authId)
                        requireActivity().finish()
                        startActivity(intent)
                    }
                }
            }
        }
        return v
    }
}