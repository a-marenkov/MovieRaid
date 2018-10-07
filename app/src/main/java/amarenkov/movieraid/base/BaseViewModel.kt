package amarenkov.movieraid.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val error = MutableLiveData<Throwable>()

    protected fun onError(throwable: Throwable?){
        throwable?.let { error.postValue(it) }
    }
}