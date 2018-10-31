package amarenkov.movieraid.di

import amarenkov.movieraid.BuildConfig
import amarenkov.movieraid.network.NetworkClient
import amarenkov.movieraid.network.TmdbApi
import amarenkov.movieraid.room.db.AppDatabase
import amarenkov.movieraid.room.repos.MoviesRepo
import amarenkov.movieraid.screens.favorite.FavoriteViewModel
import amarenkov.movieraid.screens.search.SearchViewModel
import amarenkov.movieraid.utils.SP_NAME
import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val mainModule = module {
    single { GsonBuilder().create() }
    single { androidApplication().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }
}

val viewModules = module {
    viewModel { SearchViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
}

val repoModule = module {
    single { MoviesRepo(get(), get()) }
}

val dbModule = module(createOnStart = true) {
    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, AppDatabase.NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
    single { get<AppDatabase>().genresDao() }
    single { get<AppDatabase>().moviesDao() }
}

val networkModule = module {
    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }
    single {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) builder.addInterceptor(get<HttpLoggingInterceptor>())
        builder.readTimeout(10, TimeUnit.SECONDS)
        builder.build()
    }
    single {
        Retrofit.Builder()
                .baseUrl(NetworkClient.BASE_URL)
                .client(get())
                .addConverterFactory(GsonConverterFactory.create(get()))
                .build()
    }
    single { get<Retrofit>().create(TmdbApi::class.java) }
}