package amarenkov.movieraid.room.repos

import amarenkov.movieraid.network.TmdbApi
import amarenkov.movieraid.room.models.MovieDetailed
import amarenkov.movieraid.room.models.MovieSearchResponse
import android.net.Uri

class MoviesRepo(private val db: MoviesDao, private val api: TmdbApi) {

    fun searchByName(uri: Uri, page: Int = 1): MovieSearchResponse? {
        api.searchByName(uri, page = page).execute().apply {
            if (isSuccessful) return body()
            else throw Exception("${code()} - ${message()}")
        }
        return null
    }

    fun getMovieDetails(id: Long): MovieDetailed? {
        db.get(id)?.let { return it }

        api.getMovieDetails(id).execute().apply {
            if (isSuccessful) body()?.let {
                it.roomId = save(it)
                return it
            } else throw Exception("${code()} - ${message()}")
        }
        return null
    }

    fun removeFromFavorites(id: Long) {
        db.removeFromFavorites(id)
    }

    fun setAsFavorite(id: Long) {
        db.setAsFavorite(id)
    }

    fun getFavoriteMovies() = db.getFavoriteMovies()

    fun save(movie: MovieDetailed) = db.save(movie)
    fun update(movie: MovieDetailed) = db.update(movie)


}