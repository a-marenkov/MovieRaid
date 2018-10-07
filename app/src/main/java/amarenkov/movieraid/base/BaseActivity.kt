package amarenkov.movieraid.base

import amarenkov.movieraid.R
import amarenkov.movieraid.managers.ShareManager
import amarenkov.movieraid.network.NetworkClient
import amarenkov.movieraid.utils.ifNull
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity() {

    protected abstract val viewModel: BaseViewModel
    protected val content by lazy {
        findViewById<ViewGroup>(android.R.id.content)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.error.observe(this, Observer {
            if (NetworkClient.hasConnection()) onError(it.message)
            else onNoNetwork()
        })
    }

    protected fun onNoNetwork() {
        hidePb()
        // todo show empty state screen instead
        snack(R.string.snack_no_internet)
    }

    protected fun onError(error: String?) {
        hidePb()
        // todo show empty state screen instead
        error?.let { snack(it) }.ifNull { snack(R.string.snack_error) }
    }

    protected fun start(clazz: Class<out Any>) {
        startActivity(Intent(this, clazz))
    }

    abstract fun showPb()
    abstract fun hidePb()

    protected fun snack(message: Int, duration: Int = Snackbar.LENGTH_SHORT,
                        actionText: Int? = null, actionFunc: (() -> Unit)? = null) {
        Snackbar.make(content, message, duration).apply {
            if (actionText != null && actionFunc != null) {
                setAction(actionText, { actionFunc() })
            }
            show()
        }
    }

    protected fun snack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(content, message, duration).show()
    }

    protected fun shareMovie(poster: Bitmap?, title: String, overview: String) {
        ShareManager.sendImage(this, poster, getString(R.string.share_title),
                getString(R.string.share_text, title, overview), { hidePb() })
    }
}