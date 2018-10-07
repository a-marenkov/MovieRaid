package amarenkov.movieraid.screens.search

import amarenkov.movieraid.R
import amarenkov.movieraid.base.ListCallback
import amarenkov.movieraid.managers.MovieManager
import amarenkov.movieraid.models.Movie
import amarenkov.movieraid.network.ImageSize
import amarenkov.movieraid.utils.FIRST_POSITION
import amarenkov.movieraid.utils.dozedClick
import amarenkov.movieraid.utils.load
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_movie_search.view.*

class SearchAdapter(private val callback: ListCallback<Movie>) : RecyclerView.Adapter<SearchAdapter.MovieVH>() {

    private val movies = mutableListOf<Movie>()

    override fun getItemCount() = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie_search, parent, false))

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        holder.bind(movies[position])
    }

    fun clearList() {
        val count = itemCount
        if (count == 0) return
        movies.clear()
        notifyItemRangeRemoved(FIRST_POSITION, count)
    }

    fun dispatchList(list: List<Movie>) {
        if (list.isEmpty()) {
            callback.onListEmpty()
        } else {
            movies.addAll(list)
            notifyDataSetChanged()
            callback.onListShown()
        }
    }

    fun appendList(list: List<Movie>) {
        val startPosition = itemCount
        movies.addAll(list)
        notifyItemChanged(startPosition - 1)
        notifyItemRangeInserted(startPosition, itemCount)
    }

    inner class MovieVH(root: View) : RecyclerView.ViewHolder(root) {
        fun bind(movie: Movie) {
            with(itemView) {
                ivPoster.load(movie.poster, ImageSize.W500)
                tvTitle.text = movie.titleAndYear
                tvGenres.text = MovieManager.decodeGenres(movie.genreIds)
                tvRating.text = movie.rating.toString()
                tvOverview.text = movie.overview
                btnMore.dozedClick {
                    callback.onItemSelected(movie)
                }
                itemView.dozedClick {
                    callback.onItemSelected(movie)
                }
            }
        }
    }
}


