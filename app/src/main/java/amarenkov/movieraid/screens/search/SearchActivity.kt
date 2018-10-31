package amarenkov.movieraid.screens.search

import amarenkov.movieraid.R
import amarenkov.movieraid.base.BaseViewActivity
import amarenkov.movieraid.base.ListCallback
import amarenkov.movieraid.room.models.Movie
import amarenkov.movieraid.screens.bottomsheets.DetailsBottomsheet
import amarenkov.movieraid.screens.favorite.FavoriteActivity
import amarenkov.movieraid.utils.*
import amarenkov.movieraid.utils.customs.VerticalItemDecoration
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : BaseViewActivity(), ListCallback<Movie> {

    override val viewModel: SearchViewModel by viewModel()
    private var isMoreEnabled = false
    private var isMoreShown = false
    private var isScrollUpEnabled = false
    private var isScrollUpShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val adapterMovies = SearchAdapter(this)
        with(rv) {
            layoutManager = LinearLayoutManager(this@SearchActivity, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapterMovies.setHasStableIds(true)
            adapter = adapterMovies
            addItemDecoration(VerticalItemDecoration(resources.getDimensionPixelSize(R.dimen.item_spacing_4),
                    resources.getDimensionPixelSize(R.dimen.item_bottom_spacing), this@SearchActivity, RecyclerView.VERTICAL))

            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_appear)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        isScrollUpEnabled = true
                        (recyclerView.layoutManager as? LinearLayoutManager)?.let {
                            if (it.findLastCompletelyVisibleItemPosition() == it.itemCount - 1) // scrolled to bottom
                                if (isMoreEnabled && !isMoreShown) {
                                    isMoreShown = true
                                    btnLoadMore.animate().translationY(0f).start()
                                } else if (isScrollUpEnabled && !isScrollUpShown) {
                                    isScrollUpShown = true
                                    btnScrollUp.animate().translationY(0f).start()
                                }
                        }
                    } else if (isMoreShown) {
                        isMoreShown = false
                        btnLoadMore.animate().translationY(btnLoadMore.height.toFloat() * 2).start()
                    } else if (isScrollUpShown) {
                        isScrollUpShown = false
                        btnScrollUp.animate().translationY(btnLoadMore.height.toFloat() * 2).start()
                    }
                }
            })
        }

        etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { tv, id, _ ->
            if (id == 1111 || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_UNSPECIFIED
                    || id == EditorInfo.IME_ACTION_DONE) doze(1000L) {
                if (tv.text.toString().isNotEmpty()) {
                    hideKeyboard()
                    showPb()
                    viewModel.searchByName(tv.text.toString().toUri())
                    return@OnEditorActionListener true
                }
            }
            false
        })

        etSearch.setOnFocusChangeListener({ et, hasFocus ->
            if (hasFocus) etSearch.showKeyboard()
        })

        btnFav.increaseTouchArea()
        btnFav.dozedClick {
            start(FavoriteActivity::class.java)
        }

        btnLoadMore.dozedClick {
            showPb()
            viewModel.getExtra()
        }

        btnScrollUp.dozedClick {
            rv.smoothScrollToPosition(0)
        }

        viewModel.searchResult.observe(this, Observer {
            hidePb()
            adapterMovies.clearList()
            adapterMovies.dispatchList(it)
            rv.scrollToPosition(0)
            rv.scheduleLayoutAnimation()
        })

        viewModel.searchResultExtra.observe(this, Observer {
            hidePb()
            adapterMovies.appendList(it)
        })

        viewModel.hasExtra.observe(this, Observer { isMoreEnabled = it })

        val detailsBottomsheet = layoutInflater.inflate(R.layout.bottomsheet_details, root, false) as DetailsBottomsheet
        val bottomsheetDialog = BottomSheetDialog(this, R.style.BottomsheetDialogStyle)
        bottomsheetDialog.setContentView(detailsBottomsheet)
        bottomsheetDialog.setOnCancelListener { viewModel.update(detailsBottomsheet.movie) }
        detailsBottomsheet.init({ bottomsheetDialog.cancel() }, { poster, title, overview -> shareMovie(poster, title, overview) })
        viewModel.movie.observe(this, Observer {
            detailsBottomsheet.bind(it)
            bottomsheetDialog.setState(BottomSheetBehavior.STATE_COLLAPSED)
            hidePb()
            bottomsheetDialog.show()
        })
    }

    override fun onListEmpty() {
        tvEmpty.isVisible = true
    }

    override fun onListShown() {
        tvEmpty.isVisible = false
    }

    override fun onItemSelected(item: Movie) {
        showPb()
        viewModel.getMovieDetails(item.id)
    }

    override fun onItemRemoved(item: Movie) {}

    override fun showPb() {
        pb.isVisible = true
    }

    override fun hidePb() {
        pb.isVisible = false
    }
}
