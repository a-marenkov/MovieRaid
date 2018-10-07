package amarenkov.movieraid.screens.search

import amarenkov.movieraid.utils.bg
import amarenkov.movieraid.models.Movie
import amarenkov.movieraid.repo.MoviesRepo
import amarenkov.movieraid.screens.DetailsViewModel
import android.net.Uri
import androidx.lifecycle.MutableLiveData

class SearchViewModel(repo: MoviesRepo) : DetailsViewModel(repo) {

    val searchResult = MutableLiveData<List<Movie>>()
    val searchResultExtra = MutableLiveData<List<Movie>>()
    val hasExtra = MutableLiveData<Boolean>()
    private lateinit var lastQuery: Uri
    private var page = 1

    fun searchByName(uri: Uri, page: Int = 1) = bg {
        lastQuery = uri
        repo.searchByName(uri, page)?.let {
            this@SearchViewModel.page = it.page
            hasExtra.postValue(it.page != it.pagesCount)
            if (it.page == 1) searchResult.postValue(it.results)
            else searchResultExtra.postValue(it.results)
        }
    }.invokeOnCompletion { onError(it) }


    fun getExtra() = searchByName(lastQuery, page + 1)
}