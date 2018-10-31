package amarenkov.movieraid.base

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity() {
    protected val content by lazy {
        findViewById<ViewGroup>(android.R.id.content)
    }

    protected fun start(clazz: Class<out BaseActivity>, extras: Bundle? = null) {
        startActivity(Intent(this, clazz).apply {
            extras?.let { putExtras(it) }
        })
    }

    protected fun snack(message: Int, duration: Int = Snackbar.LENGTH_SHORT,
                        actionText: Int? = null, actionFunc: (() -> Unit)? = null) =
            with(Snackbar.make(content, message, duration)) {
                if (actionText != null && actionFunc != null) {
                    setAction(actionText) { actionFunc() }
                }
                show()
            }

    protected fun snack(message: String, duration: Int = Snackbar.LENGTH_SHORT,
                        actionText: String? = null, actionFunc: (() -> Unit)? = null) =
            with(Snackbar.make(content, message, duration)) {
                if (actionText != null && actionFunc != null) {
                    setAction(actionText) { actionFunc() }
                }
                show()
            }
}