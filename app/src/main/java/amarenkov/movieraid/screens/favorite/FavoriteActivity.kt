package amarenkov.movieraid.screens.favorite

import amarenkov.movieraid.R
import amarenkov.movieraid.base.BaseViewActivity
import amarenkov.movieraid.base.ListCallback
import amarenkov.movieraid.room.models.Movie
import amarenkov.movieraid.screens.bottomsheets.DetailsBottomsheet
import amarenkov.movieraid.utils.setState
import amarenkov.movieraid.utils.switchText
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favorite.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteActivity : BaseViewActivity(), ListCallback<Movie> {

    override val viewModel by viewModel<FavoriteViewModel>()
    private val adapter by lazy {
        FavoriteMoviesAdapter(this) { tvHeader.switchText(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })

        with(rv) {
            layoutManager = LinearLayoutManager(this@FavoriteActivity, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            this@FavoriteActivity.adapter.setHasStableIds(true)
            adapter = this@FavoriteActivity.adapter

            ItemTouchHelper(SwipeTouchHelperCallback(this@FavoriteActivity.adapter)).also {
                it.attachToRecyclerView(this)
            }

            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_end)

            rv.set(item_movie_favorite, 0)
            item_movie_favorite.post {
                tvHeader.layoutParams = tvHeader.layoutParams.apply { width = item_movie_favorite.width }
            }
        }

        viewModel.movies.observe(this, Observer {
            hidePb()
            adapter.clearList()
            adapter.dispatchList(it)
            rv.scheduleLayoutAnimation()
        })

        val detailsBottomsheet = layoutInflater.inflate(R.layout.bottomsheet_details, content, false) as DetailsBottomsheet
        val bottomsheetDialog = BottomSheetDialog(this, R.style.BottomsheetDialogStyle)
        bottomsheetDialog.setContentView(detailsBottomsheet)
        bottomsheetDialog.setOnCancelListener {
            detailsBottomsheet.movie.let {
                viewModel.update(it)
                if (!it.isFavorite) adapter.remove(it)
            }
        }
        detailsBottomsheet.init({ bottomsheetDialog.cancel() }, { poster, title, overview -> shareMovie(poster, title, overview) })
        viewModel.movie.observe(this, Observer {
            detailsBottomsheet.bind(it)
            bottomsheetDialog.setState(BottomSheetBehavior.STATE_COLLAPSED)
            hidePb()
            bottomsheetDialog.show()
        })

        showPb()
        viewModel.getFavoriteMovies()
    }

    override fun showPb() {
        pb.isVisible = true
    }

    override fun hidePb() {
        pb.isVisible = false
    }

    override fun onItemSelected(item: Movie) {
        showPb()
        viewModel.getMovieDetails(item.id)
    }

    override fun onItemRemoved(item: Movie) {
        viewModel.removeFromFavorites(item.id)
        snack(R.string.snack_removed_from_favs, Snackbar.LENGTH_LONG, R.string.cancel, {
            rv.smoothSnapToPosition(adapter.undoRemoval())
            viewModel.setAsFavorite(item.id)
        })
    }

    override fun onListEmpty() {
        tvEmpty.isVisible = true
    }

    override fun onListShown() {
        tvEmpty.isVisible = false
    }
}

