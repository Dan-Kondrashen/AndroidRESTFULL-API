package ru.kondrashin.myworkingproject.api


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.kondrashin.myworkingproject.AddBlog
import ru.kondrashin.myworkingproject.Blog

interface BlogsApi {
    @GET("api/blogs")
    suspend fun getBlogsAsync(): List<Blog>
//    fun getBlogsAsync(): Call<List<Blog>>
    @POST("api/blogs")
    suspend fun postBlogAsync(@Body postBlogRequest: AddBlog)


}