package amarenkov.movieraid.utils

import amarenkov.movieraid.R
import amarenkov.movieraid.network.ImageSize
import amarenkov.movieraid.network.NetworkClient
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.TouchDelegate
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import org.koin.core.KoinContext
import org.koin.standalone.StandAloneContext

fun <T> Any.ui(block: suspend CoroutineScope.() -> T): Deferred<T> = GlobalScope.async(Dispatchers.Main, block = block)

inline fun <reified T : Any> getFromKoin(name: String = "") = (StandAloneContext.koinContext as KoinContext).get<T>(name)

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(getWindowToken(), 0)
}

fun Activity.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

val String.logd: String
    get() {
        Log.d("_logd: ", this)
        return this
    }

val sp: SharedPreferences
    get() = getFromKoin()

fun Context.placeholder(): CircularProgressDrawable = CircularProgressDrawable(this)
        .apply {
            setColorSchemeColors(
                    ContextCompat.getColor(this@placeholder, R.color.colorAccent),
                    ContextCompat.getColor(this@placeholder, R.color.colorPrimary),
                    ContextCompat.getColor(this@placeholder, R.color.lightBlue))
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }

fun View.dozedClick(delay: Long = 500L, block: (View) -> Unit) {
    setOnClickListener { doze(delay) { block(it) } }
}

fun ImageView.load(url: String?, size: ImageSize) {
    url?.let {
        if (url.isBlank()) isVisible = false
        else {
            isVisible = true
            Glide.with(context).load(NetworkClient.IMG_URL + size.size + it)
                    .apply(RequestOptions()
                            .centerCrop()
                            .placeholder(context.placeholder()))
                    .into(this)
        }
    }.ifNull { isVisible = false }
}

fun BottomSheetDialog.setState(state: Int = BottomSheetBehavior.STATE_COLLAPSED) {
    BottomSheetBehavior.from(findViewById<FrameLayout>(R.id.design_bottom_sheet)).state = state
}

fun View.increaseTouchArea() {
    //Increase touch area
    val parent = parent as View  // button: the view you want to enlarge hit area
    parent.post {
        val rect = Rect()
        getHitRect(rect)
        rect.top -= 100    // increase top hit area
        rect.left -= 100   // increase left hit area
        rect.bottom += 100 // increase bottom hit area
        rect.right += 100  // increase right hit area
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}

fun getPluralString(resources: Resources, value: Int, id0: Int, id1: Int, id2: Int): String {
    val base = value % 100
    val lastPart = base % 10
    return when {
        lastPart == 0 || base in 5..20 || lastPart in 5..9 -> resources.getString(id0)
        lastPart in 2..4 -> resources.getString(id2)
        else -> resources.getString(id1)
    }
}

fun TextView.setVisibilityAndText(text: String) {
    this.isVisible = text.isNotBlank()
    this.text = text
}

fun TextView.switchText(text: String) {
    if (this.text == text) return
    if (alpha != 0f) animate().alpha(0f)
            .withEndAction {
                this.text = text
                if (text.isNotBlank()) animate().alpha(1f).start()
            }
            .start()
    else animate().alpha(1f)
            .withStartAction { this.text = text }
            .start()
}