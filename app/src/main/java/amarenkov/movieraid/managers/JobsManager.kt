package amarenkov.movieraid.managers

import amarenkov.movieraid.managers.jobs.UpdateJob
import amarenkov.movieraid.utils.sp
import android.content.Context
import androidx.core.content.edit
import com.firebase.jobdispatcher.*

object JobsManager {
    const val MINUTE = 60
    const val HOUR = MINUTE * 60
    const val DAY = HOUR * 24
    const val WEEK = DAY * 7

    fun scheduleRecurring(context: Context, service: AppJob) {
        if (sp.getBoolean(service.clazz.simpleName, false)) return
        FirebaseJobDispatcher(GooglePlayDriver(context)).let {
            val job = it.newJobBuilder()
                    .setService(service.clazz as Class<out JobService>?)
                    .setTag(service.clazz.simpleName)
                    .setRecurring(true)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(service.startTime, service.interval))
                    .setReplaceCurrent(true)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build()
            it.mustSchedule(job)
        }
        sp.edit { putBoolean(service.clazz.simpleName, true) }
    }

    fun cancel(dispatcher: FirebaseJobDispatcher, service: AppJob) {
        dispatcher.cancel(service.clazz.simpleName)
        sp.edit { putBoolean(service.clazz.simpleName, false) }
    }
}

enum class AppJob(val clazz: Class<out JobService>, val startTime: Int, val interval: Int) {
    UPDATE_JOB(UpdateJob::class.java, JobsManager.HOUR, JobsManager.DAY)
}