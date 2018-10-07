package amarenkov.movieraid

import amarenkov.movieraid.di.*
import android.app.Application
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(mainModule, viewModules, repoModule, dbModule, networkModule))
    }
}