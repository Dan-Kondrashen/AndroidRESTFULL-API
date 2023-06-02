package ru.kondrashin.myworkingproject.api

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.internal.bind.DateTypeAdapter
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiFactory {
    private val gsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
//        .setLenient()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://9031-85-249-160-100.ngrok-free.app/")
        .client(getUnsafeOkHttpClient())
        .addConverterFactory(
            GsonConverterFactory.create(
            gsonBuilder.create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    val blogApi = retrofit.create(BlogsApi::class.java)
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not
            // validate certificate chains
            val trustAllCerts: Array<TrustManager> =
                arrayOf<TrustManager>(object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String?
                    ) {
                    }
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String?
                    ) {
                    }
                    override fun getAcceptedIssuers():
                            Array<X509Certificate> =
                        arrayOf()
                })
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory =
                sslContext.socketFactory
            val httpClient = OkHttpClient.Builder()
            httpClient.sslSocketFactory(sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
                .hostnameVerifier { hostname, session -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
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