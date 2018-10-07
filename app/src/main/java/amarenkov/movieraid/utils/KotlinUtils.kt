package amarenkov.movieraid.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async

val now: Long
    get() = System.currentTimeMillis()

fun <T> Any.bg(block: suspend CoroutineScope.() -> T): Deferred<T> = GlobalScope.async(block = block)

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

inline fun Any?.ifNull(function: () -> Unit) {
    if (this == null) function.invoke()
}

var nextAllowedEventTime = 0L
inline fun doze(delay: Long = 500L, block: () -> Unit) {
    if (now > nextAllowedEventTime) {
        nextAllowedEventTime = now + delay
        block()
    }
}

fun Int.convertToMoneyString()= with(StringBuilder()) {
    val number = this@convertToMoneyString.toString()
    val remainder = number.length % 3
    if(number.length < 4) return@with number
    else number.forEachIndexed { index: Int, char: Char ->
        append(char)
        if(index % 3 == remainder - 1 && index != number.length - 1) append(',')
    }
    return@with toString()
}