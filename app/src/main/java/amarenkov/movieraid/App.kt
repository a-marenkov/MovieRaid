package amarenkov.movieraid

import amarenkov.movieraid.di.*
import amarenkov.movieraid.workers.AppWorker
import amarenkov.movieraid.workers.UpdateWorker
import amarenkov.movieraid.workers.scheduleRecurring
import android.app.Application
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(mainModule, viewModules, repoModule, dbModule, networkModule))

        scheduleRecurring<UpdateWorker>(this, AppWorker.UPDATE_WORKER)

    }
}