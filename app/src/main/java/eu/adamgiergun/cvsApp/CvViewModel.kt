package eu.adamgiergun.cvsApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class CvApiStatus { LOADING, ERROR, DONE }

internal class CvViewModel : ViewModel() {

    private val _status = MutableLiveData<CvApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<CvApiStatus>
        get() = _status

    private val _cv = MutableLiveData<List<CvItem>>()
    val cv: LiveData<List<CvItem>>
        get() = _cv

    private var vieModelJob = Job()
    private var coroutineScope = CoroutineScope(vieModelJob + Dispatchers.Main)

    init {
        getCv()
    }

    private fun getCv() {
        coroutineScope.launch {
            try {
                _status.value = CvApiStatus.LOADING
                val listResult = CvApi.retrofitService.getCv()
                _status.value = CvApiStatus.DONE
                _cv.value = listResult
            } catch (e: Exception) {
                _status.value = CvApiStatus.ERROR
                _cv.value = ArrayList()
            }
        }
    }
}