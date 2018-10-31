package amarenkov.movieraid.base

import amarenkov.movieraid.R
import amarenkov.movieraid.managers.ShareManager
import amarenkov.movieraid.network.NetworkClient
import amarenkov.movieraid.utils.ifNull
import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.Observer

abstract class BaseViewActivity : BaseActivity() {

    protected abstract val viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.error.observe(this, Observer { onError(it.message) })
    }

    protected fun onError(error: String?) {
        hidePb()
        when {
            !NetworkClient.hasConnection() -> snack(R.string.snack_no_internet)
            else -> {
                error?.let { snack(it) }.ifNull { snack(R.string.snack_unknown_error) }
            }
        }
    }

    protected abstract fun showPb()

    protected abstract fun hidePb()

    protected fun shareMovie(poster: Bitmap?, title: String, overview: String) {
        ShareManager.sendImage(this, poster, getString(R.string.share_title),
                getString(R.string.share_text, title, overview), { hidePb() })
    }
}