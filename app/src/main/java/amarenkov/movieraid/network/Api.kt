package amarenkov.movieraid.network

import amarenkov.movieraid.models.Configuration
import amarenkov.movieraid.models.GenresResponse
import amarenkov.movieraid.models.MovieDetailed
import amarenkov.movieraid.models.MovieSearchResponse
import android.net.Uri
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("/3/search/movie?api_key=${NetworkClient.API_KEY}")
    fun searchByName(@Query("query") query: Uri,
                     @Query("page") page: Int = 1,
                     @Query("language") language: String = "ru-RU",
                     @Query("include_adult") includeAdults: Boolean = false): Call<MovieSearchResponse>

    @GET("/3/genre/movie/list?api_key=${NetworkClient.API_KEY}")
    fun getGenres(@Query("language") language: String = "ru-RU"): Call<GenresResponse>

    @GET("/3/movie/{id}?api_key=${NetworkClient.API_KEY}&append_to_response=credits")
    fun getMovieDetails(@Path("id") id: Long, @Query("language") language: String = "ru-RU"): Call<MovieDetailed>

    @GET("3/configuration?api_key=${NetworkClient.API_KEY}")
    fun getConfiguration(): Call<Configuration>
}