package amarenkov.movieraid.managers

import amarenkov.movieraid.managers.workers.UpdateWorker
import amarenkov.movieraid.utils.sp
import androidx.core.content.edit
import androidx.work.*
import java.util.concurrent.TimeUnit

@Deprecated("jobdispatcher should be used instead")
object WorkerManager {
    @Deprecated("jobdispatcher should be used instead")
    inline fun <reified T : Worker> scheduleRecurringWork(appWorker: AppWorker) {
        if (sp.getBoolean(appWorker.clazz.simpleName, false)) return

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .setRequiresBatteryNotLow(true)
                .build()

        PeriodicWorkRequestBuilder<T>(appWorker.interval, TimeUnit.MILLISECONDS)
                .addTag(appWorker.clazz.simpleName)
                .setConstraints(constraints)
                .build().let {
                    WorkManager.getInstance().enqueueUniquePeriodicWork(appWorker.clazz.simpleName,
                            ExistingPeriodicWorkPolicy.REPLACE, it)
                }
        sp.edit { putBoolean(appWorker.clazz.simpleName, true) }
    }

    @Deprecated("jobdispatcher should be used instead")
    fun cancelWork(appWorker: AppWorker) {
        WorkManager.getInstance().cancelAllWorkByTag(appWorker.clazz.simpleName)
        sp.edit { putBoolean(appWorker.clazz.simpleName, true) }
    }
}


@Deprecated("jobdispatcher should be used instead")
enum class AppWorker(val clazz: Class<out Worker>, val startTime: Long, val interval: Long) {
    UPDATE_WORKER(UpdateWorker::class.java, TimeUnit.DAYS.toMillis(1), TimeUnit.DAYS.toMillis(1))
}