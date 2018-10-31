package amarenkov.movieraid.screens.favorite

import amarenkov.movieraid.R
import amarenkov.movieraid.base.ListCallback
import amarenkov.movieraid.network.ImageSize
import amarenkov.movieraid.room.models.Movie
import amarenkov.movieraid.room.models.MovieDetailed
import amarenkov.movieraid.utils.FIRST_POSITION
import amarenkov.movieraid.utils.customs.SnappyAdapter
import amarenkov.movieraid.utils.dozedClick
import amarenkov.movieraid.utils.load
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.android.synthetic.main.item_movie_favorite.view.*

class FavoriteMoviesAdapter(private val callback: ListCallback<Movie>,
                            private val onItemFocused: (String) -> Unit) : SnappyAdapter<FavoriteMoviesAdapter.MovieVH>() {

    private val movies = mutableListOf<Movie>()
    private var removedMovie: Movie? = null
    private var removedMoviePosition = FIRST_POSITION
    private var focusedItem = -1

    override fun onItemCentered(position: Int) {
        focusedItem = position
        onItemFocused(if (position == NO_POSITION) "" else movies[position].titleAndYear)
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

    fun undoRemoval(): Int {
        removedMovie?.let {
            movies.add(removedMoviePosition, it)
            notifyItemInserted(removedMoviePosition)
            removedMovie = null
        }
        return removedMoviePosition
    }

    override fun getItemCount() = movies.size

    override fun getItemId(position: Int) = movies[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
        return MovieVH(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie_favorite, parent, false))
    }

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        holder.onBind(movies[position])
    }

    fun remove(movie: MovieDetailed) {
        movies.indexOfFirst { it.id == movie.id }.let {
            if (it == NO_POSITION) return
            movies.removeAt(it)
            notifyItemRemoved(it)
        }
    }

    fun remove(viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as? MovieVH)?.remove()
    }

    inner class MovieVH(root: View) : RecyclerView.ViewHolder(root) {
        init {
            root.dozedClick {
                callback.onItemSelected(movies[adapterPosition])
                if (adapterPosition != focusedItem) mScroller?.smoothSnapToPosition(adapterPosition)
            }
        }

        fun onBind(movie: Movie) {
            itemView.ivPoster.load(movie.poster, ImageSize.ORIGINAL)
            itemView.tvRating.text = movie.rating.toString()
        }

        fun remove() {
            removedMoviePosition = adapterPosition
            callback.onItemRemoved(movies[adapterPosition])
            removedMovie = movies.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
        }
    }
}

class SwipeTouchHelperCallback(private val adapter: FavoriteMoviesAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
            makeMovementFlags(0, ItemTouchHelper.UP)

    override fun isLongPressDragEnabled() = false
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder) = false


    override fun isItemViewSwipeEnabled() = true


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.remove(viewHolder)
    }
}