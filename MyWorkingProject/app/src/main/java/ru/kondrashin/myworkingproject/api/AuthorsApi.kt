package ru.kondrashin.myworkingproject.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.kondrashin.myworkingproject.AddBlog
import ru.kondrashin.myworkingproject.Author
import ru.kondrashin.myworkingproject.AddAuthor
import ru.kondrashin.myworkingproject.Blog
import ru.kondrashin.myworkingproject.LogAuthor
import ru.kondrashin.myworkingproject.ServerResponse
import ru.kondrashin.myworkingproject.UpdateAuthor

interface AuthorsApi {
    @GET("authors")
    suspend fun getAuthorsAsync(): List<Author>

    @POST("registration")
    suspend fun regAuthorAsync(@Body postAuthorRequest: AddAuthor): ServerResponse
    @POST("login")
    suspend fun logAuthorAsync(@Body postAuthorRequest: LogAuthor): ServerResponse
    @GET("author/{author_id}")
    suspend fun getAuthorAsync(@Path("author_id") id: Int): Author
    @PUT("author/{author_id}")
    suspend fun putAuthorAsync(@Path("author_id") id: Int, @Body putAuthorRequest: UpdateAuthor): ServerResponse
    @DELETE("author/{author_id}")
    suspend fun deleteAuthorAsync(@Path("author_id") id: Int)


}