package amarenkov.movieraid.utils

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async

val now: Long
    get() = System.currentTimeMillis()

fun <T> Any.bg(block: suspend CoroutineScope.() -> T): Deferred<T> = GlobalScope.async(block = block)

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