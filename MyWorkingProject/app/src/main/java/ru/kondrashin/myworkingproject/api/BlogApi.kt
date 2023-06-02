package ru.kondrashin.myworkingproject.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.kondrashin.myworkingproject.Blog
import ru.kondrashin.myworkingproject.ServerResponse

interface BlogApi {
    @PUT("api/blogs/{blog_id}")
    suspend fun putBlogAsync(@Path("blog_id") id: Int, @Body putBlogRequest: Blog): ServerResponse
    @DELETE("api/blogs/{blog_id}")
    suspend fun deleteBlogAsync(@Path("blog_id") id: Int)


}