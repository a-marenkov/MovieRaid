package amarenkov.movieraid.workers

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

inline fun <reified T: Worker> scheduleRecurring(context: Context, appWorker: AppWorker) {
    WorkManager.getInstance().getStatusesByTag(appWorker.clazz.simpleName).let {
        if(it.value?.isNotEmpty() ?: false) return
    }

    val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .build()

    PeriodicWorkRequestBuilder<T>(appWorker.interval, TimeUnit.MILLISECONDS)
            .addTag(appWorker.clazz.simpleName)
            .setConstraints(constraints)
            .build().let {
                WorkManager.getInstance().enqueue(it)
            }
}

enum class AppWorker(val clazz: Class<out Worker>, val startTime: Long, val interval: Long) {
    UPDATE_WORKER(UpdateWorker::class.java, TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(15))
}