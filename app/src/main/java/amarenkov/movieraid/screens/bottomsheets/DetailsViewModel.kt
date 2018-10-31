package amarenkov.movieraid.screens.bottomsheets

import amarenkov.movieraid.base.BaseViewModel
import amarenkov.movieraid.base.EventLiveData
import amarenkov.movieraid.room.models.MovieDetailed
import amarenkov.movieraid.room.repos.MoviesRepo
import amarenkov.movieraid.utils.bg

abstract class DetailsViewModel(protected val repo: MoviesRepo) : BaseViewModel() {

    val movie = EventLiveData<MovieDetailed>()

    fun getMovieDetails(id: Long) = bg {
        repo.getMovieDetails(id)?.let {
            movie.postValue(it)
        }
    }.invokeOnCompletion { onError(it) }

    fun update(movie: MovieDetailed) = bg { repo.update(movie) }.invokeOnCompletion { onError(it) }
}