package amarenkov.movieraid.screens

import amarenkov.movieraid.R
import amarenkov.movieraid.models.MovieDetailed
import amarenkov.movieraid.network.ImageSize
import amarenkov.movieraid.utils.dozedClick
import amarenkov.movieraid.utils.increaseTouchArea
import amarenkov.movieraid.utils.load
import amarenkov.movieraid.utils.setVisibilityAndText
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottomsheet_details.view.*
import kotlinx.android.synthetic.main.toolbar_bottomsheet.view.*

class DetailsBottomsheet : CoordinatorLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    lateinit var movie: MovieDetailed

    fun init(onClose: () -> Unit, onShare: (Bitmap?, String, String) -> Unit) {
        scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if (scrollY != 0 && !toolbar.isVisible) toolbar.animate().alpha(1f).setDuration(300L)
                        .withStartAction { toolbar.isVisible = true }
                        .start()
                else if (scrollY == 0 && toolbar.isVisible) toolbar.animate().alpha(0f).setDuration(300L)
                        .withEndAction { toolbar.isVisible = false }
                        .start()
            }
        })

        toolbar.btnClose.increaseTouchArea()
        toolbar.btnClose.dozedClick {
            onClose()
            toolbar.isVisible = false
            toolbar.alpha = 0f
        }

        ivFavorite.increaseTouchArea()
        ivFavorite.dozedClick {
            movie.isFavorite = !movie.isFavorite
            toggleFavorite()
        }

        ivShare.increaseTouchArea()
        ivShare.dozedClick {
            onShare.invoke(ivPoster.drawable.toBitmap(), movie.title, movie.overview)
        }
    }

    fun bind(movie: MovieDetailed) {
        scrollView.scrollY = 0
        this.movie = movie
        with(movie) {
            toolbar.toolbarTitle.text = title

            ivPoster.load(poster, ImageSize.W500)
            if(backdrop.isNullOrBlank()) holder_backdrop.isVisible = false
            else {
                holder_backdrop.isVisible = true
                ivBackdrop.load(backdrop, ImageSize.ORIGINAL)
            }
            tvChip.text = getRating(resources)
            tvDuration.text = getDuration(resources)
            tvGenre.text = getGenres(resources)
            tvBudget.text = getBudget(resources)
            tvTitle.text = title
            tvTitleOriginal.isVisible = titleOriginal != title
            tvTitleOriginal.text = titleOriginal
            tvOverview.setVisibilityAndText(overview)
            tvTagline.setVisibilityAndText(tagline)
            tvCast.text = getCast(resources)
            tvCrew.text = getCrew(resources)
            toggleFavorite(false)
        }
    }

    private fun toggleFavorite(showSnackbar: Boolean = true) {
        if (movie.isFavorite) {
            ivFavorite.setImageResource(R.drawable.ic_fav)
            if(showSnackbar)
                Snackbar.make(this, R.string.snack_added_to_favs, Snackbar.LENGTH_SHORT).show()
        } else {
            ivFavorite.setImageResource(R.drawable.ic_unfav)
            if(showSnackbar)
                Snackbar.make(this, R.string.snack_removed_from_favs, Snackbar.LENGTH_SHORT).show()
        }
    }
}


