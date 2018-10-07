package amarenkov.movieraid.managers.jobs

import amarenkov.movieraid.managers.MovieManager
import amarenkov.movieraid.utils.bg
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class UpdateJob : JobService() {
    override fun onStopJob(job: JobParameters): Boolean {
        return true
    }

    override fun onStartJob(job: JobParameters): Boolean {
        bg {
            MovieManager.updateGenres()
            MovieManager.clearCacheAndUpdate()
        }.invokeOnCompletion {
            it?.let {
                it.printStackTrace()
                jobFinished(job, true)
            }
        }
        return false
    }
}