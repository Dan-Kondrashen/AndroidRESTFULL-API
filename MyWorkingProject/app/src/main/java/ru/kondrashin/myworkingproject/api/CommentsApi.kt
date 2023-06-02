package ru.kondrashin.myworkingproject.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.kondrashin.myworkingproject.AddBlog
import ru.kondrashin.myworkingproject.AddComment
import ru.kondrashin.myworkingproject.Comment
import ru.kondrashin.myworkingproject.ServerResponse

interface CommentsApi {
    @GET("api/blogs/{blog_id}/comments")
    suspend fun getCommentsAsync(@Path("blog_id") id: Int): List<Comment>
    @POST("api/blogs/{blog_id}/comments")
    suspend fun postCommentAsync(@Path("blog_id") id: Int, @Body postCommentRequest: AddComment)
    @DELETE("api/comments/{comm_id}")
    suspend fun deleteCommAsync(@Path("comm_id") id: Int)
    @PUT("api/comments/{comm_id}")
    suspend fun putCommAsync(@Path("comm_id") id: Int, @Body putCommentRequest: Comment): ServerResponse
}