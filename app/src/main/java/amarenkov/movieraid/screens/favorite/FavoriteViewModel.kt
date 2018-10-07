package amarenkov.movieraid.screens.favorite

import amarenkov.movieraid.models.Movie
import amarenkov.movieraid.repo.MoviesRepo
import amarenkov.movieraid.screens.DetailsViewModel
import amarenkov.movieraid.utils.bg
import androidx.lifecycle.MutableLiveData

class FavoriteViewModel(repo: MoviesRepo) : DetailsViewModel(repo) {

    val movies = MutableLiveData<List<Movie>>()

    fun getFavoriteMovies() = bg {
        movies.postValue(repo.getFavoriteMovies())
    }.invokeOnCompletion {
        onError(it)
    }

    fun removeFromFavorites(id: Long) = bg { repo.removeFromFavorites(id) }.invokeOnCompletion { onError(it) }


    fun setAsFavorite(id: Long) = bg { repo.setAsFavorite(id) }.invokeOnCompletion { onError(it) }
}