package amarenkov.movieraid.workers

import amarenkov.movieraid.managers.MovieManager
import amarenkov.movieraid.network.NetworkClient
import amarenkov.movieraid.utils.logd
import amarenkov.movieraid.utils.sp
import android.content.Context
import androidx.core.content.edit
import androidx.work.Worker
import androidx.work.WorkerParameters

class UpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
       private const val SUCCESS = "SUCCESS_UpdateWorker"
       private const val FAILURE = "FAILURE_UpdateWorker"
    }


    override fun doWork(): Result {
        try {
            MovieManager.updateGenres()
            MovieManager.clearCacheAndUpdate()

            var count = sp.getInt(SUCCESS, 0)
            sp.edit {
                putInt(SUCCESS, count++)
            }

        } catch (t: Throwable) {

            var count = sp.getInt(FAILURE, 0)
            sp.edit {
                putInt(FAILURE, count++)
            }

            t.printStackTrace()
            t.message?.logd
            if (NetworkClient.hasConnection()) return Result.RETRY
            return Result.FAILURE
        }
        return Result.SUCCESS
    }
}