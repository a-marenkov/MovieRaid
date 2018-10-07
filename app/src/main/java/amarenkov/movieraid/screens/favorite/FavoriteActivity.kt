package amarenkov.movieraid.screens.favorite

import amarenkov.movieraid.R
import amarenkov.movieraid.base.BaseActivity
import amarenkov.movieraid.base.ListCallback
import amarenkov.movieraid.models.Movie
import amarenkov.movieraid.screens.DetailsBottomsheet
import amarenkov.movieraid.utils.FIRST_POSITION
import amarenkov.movieraid.utils.customs.HorizontalItemDecoration
import amarenkov.movieraid.utils.setState
import amarenkov.movieraid.utils.switchText
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
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


class FavoriteActivity : BaseActivity(), ListCallback<Movie> {

    override val viewModel by viewModel<FavoriteViewModel>()
    private val adapter by lazy { FavoriteMoviesAdapter(this) }

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
            adapter = this@FavoriteActivity.adapter
            onItemSelected = { position ->
                tvHeader.switchText(this@FavoriteActivity.adapter.getLabel(position))
            }
            val dpWidth = resources.displayMetrics.widthPixels
            val cardWidth = resources.displayMetrics.heightPixels * 0.65f * 0.65f
            val edgeOffset = (dpWidth - cardWidth) / 2

            addItemDecoration(HorizontalItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_spacing_8),
                    edgeOffset.toInt(),
                    this@FavoriteActivity,
                    LinearLayout.HORIZONTAL))

            ItemTouchHelper(SwipeTouchHelperCallback(this@FavoriteActivity.adapter)).also {
                it.attachToRecyclerView(this)
            }

            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_end)

            tvHeader.layoutParams = tvHeader.layoutParams.apply { width = cardWidth.toInt() }
        }

        viewModel.movies.observe(this, Observer {
            hidePb()
            adapter.clearList()
            rv.scrollToPosition(FIRST_POSITION)
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
        detailsBottomsheet.enableToolbar { bottomsheetDialog.cancel() }
        detailsBottomsheet.onShare = { poster, title, overview -> shareMovie(poster, title, overview) }
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

    override fun onItemRemove(item: Movie) {
        viewModel.removeFromFavorites(item.id)
        snack(R.string.snack_removed_from_favs, Snackbar.LENGTH_LONG, R.string.cancel, {
            rv.smoothSnapTo(adapter.undoRemoval())
            viewModel.setAsFavorite(item.id)
        })
    }

    override fun onListEmpty() {
        tvEmpty.isVisible = true
    }

    override fun onListShown() {
        tvEmpty.isVisible = false
        rv.onItemSelected?.invoke(FIRST_POSITION)
    }
}

