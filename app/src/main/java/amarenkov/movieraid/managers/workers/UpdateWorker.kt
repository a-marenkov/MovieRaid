package amarenkov.movieraid.managers.workers

import amarenkov.movieraid.managers.MovieManager
import amarenkov.movieraid.network.NetworkClient
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            MovieManager.updateGenres()
            MovieManager.clearCacheAndUpdate()
        } catch (t: Throwable) {
            if (NetworkClient.hasConnection()) return Result.RETRY
            return Result.FAILURE
        }
        return Result.SUCCESS
    }
}