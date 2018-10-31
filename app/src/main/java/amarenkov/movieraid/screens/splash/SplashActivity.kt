package amarenkov.movieraid.screens.splash

import amarenkov.movieraid.R
import amarenkov.movieraid.managers.AppJob
import amarenkov.movieraid.managers.JobsManager
import amarenkov.movieraid.managers.MovieManager
import amarenkov.movieraid.network.NetworkClient
import amarenkov.movieraid.screens.search.SearchActivity
import amarenkov.movieraid.utils.bg
import amarenkov.movieraid.utils.ifNull
import amarenkov.movieraid.utils.sp
import amarenkov.movieraid.utils.ui
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar

class SplashActivity : AppCompatActivity() {

    private var isFirstStart: Boolean
        get() = sp.getBoolean("isFirstStart", true)
        set(value) = sp.edit { putBoolean("isFirstStart", value) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    fun init() {
        bg { JobsManager.scheduleRecurring(this@SplashActivity, AppJob.UPDATE_JOB) }
        bg {
            MovieManager.updateEndpoints()
            MovieManager.updateGenres(isFirstStart)
        }.invokeOnCompletion {
            it?.let {
                if (NetworkClient.hasConnection()) snack(it.message
                        ?: getString(R.string.snack_unknown_error))
                else snack(getString(R.string.snack_no_internet), Snackbar.LENGTH_INDEFINITE, getString(R.string.repeat), { init() })
            }.ifNull {
                proceed()
                return@invokeOnCompletion
            }
            if (!isFirstStart) Handler().postDelayed({
                proceed()
            }, 2000L)
        }
    }

    fun proceed() {
        isFirstStart = false
        setTheme(R.style.AppTheme)
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }

    private fun snack(message: String, duration: Int = Snackbar.LENGTH_SHORT,
                      actionText: String? = null, actionFunc: (() -> Unit)? = null) = ui {
        Snackbar.make(findViewById(android.R.id.content), message, duration).apply {
            if (actionText != null && actionFunc != null) {
                setAction(actionText, { actionFunc() })
            }
            show()
        }
    }
}