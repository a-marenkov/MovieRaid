package amarenkov.movieraid.managers

import amarenkov.movieraid.network.NetworkClient.KEY_IMG_URL
import amarenkov.movieraid.network.TmdbApi
import amarenkov.movieraid.repo.MoviesDao
import amarenkov.movieraid.room.dao.GenresDao
import amarenkov.movieraid.utils.getFromKoin
import amarenkov.movieraid.utils.now
import amarenkov.movieraid.utils.sp
import android.text.format.DateUtils
import androidx.core.content.edit

object MovieManager {

    private const val CACHE_TIME = DateUtils.WEEK_IN_MILLIS
    private val genres: MutableMap<Int, String> = mutableMapOf()
    private val dbGenres by lazy { getFromKoin<GenresDao>() }
    private val dbMovies by lazy { getFromKoin<MoviesDao>() }
    private val api by lazy { getFromKoin<TmdbApi>() }

    fun updateGenres(forceUpdate: Boolean = true) {
        if (forceUpdate) api.getGenres().execute().apply {
            if (isSuccessful) body()?.let {
                dbGenres.saveAll(it.genres)
            } else throw Exception("${this@apply.code()} - ${this@apply.message()}")
        }

        dbGenres.getAll().let {
            genres.putAll(it.associateBy({ it.id }, { it.name }))
        }
    }

    fun updateEndpoints() {
        api.getConfiguration().execute().apply {
            if (isSuccessful) body()?.let {
                sp.edit { putString(KEY_IMG_URL, it.images.baseUrl) }
            } else throw Exception("${this@apply.code()} - ${this@apply.message()}")
        }
    }

    fun clearCacheAndUpdate() {
        dbMovies.clearCache(now - CACHE_TIME)
        dbMovies.getAll().forEach {
            api.getMovieDetails(it).execute().body()?.let {
                dbMovies.update(it)
            }
        }
    }

    fun decodeGenres(ids: List<Int>) = with(StringBuilder("")) {
        ids.forEach {
            genres.get(it)?.let {
                append(it)
                append(", ")
            }
        }
        if (isBlank()) return@with toString()
        else return@with toString().substring(0, length - 2)
    }
}