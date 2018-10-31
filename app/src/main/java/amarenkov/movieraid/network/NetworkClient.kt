package amarenkov.movieraid.network

import amarenkov.movieraid.utils.getFromKoin
import amarenkov.movieraid.utils.sp
import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService

object NetworkClient {
    const val BASE_URL = "https://api.themoviedb.org"
    const val API_KEY = "59a4b0823daac10ef8a00b9730959966"
    const val KEY_IMG_URL = "KEY_IMG_URL"
    val IMG_URL by lazy {
        sp.getString(KEY_IMG_URL, "https://image.tmdb.org/t/p/") ?: "https://image.tmdb.org/t/p/"
    }

    fun hasConnection() = getFromKoin<Context>().getSystemService<ConnectivityManager>()
            ?.activeNetworkInfo != null
}

enum class ImageSize(val size: String) {
    ORIGINAL("original"), W500("w500")
}

