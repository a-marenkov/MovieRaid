package amarenkov.movieraid.base

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class EventLiveData<T> : MutableLiveData<T>() {
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { event ->
            if (event == null) return@Observer
            observer.onChanged(event)
            value = null
        })
    }
}