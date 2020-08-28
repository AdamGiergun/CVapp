package eu.adamgiergun.cvsApp

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class ViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var cvDataSource: CvDataSource

    private val _cv = MutableLiveData<CV>()
    val cv: LiveData<CV>
        get() = _cv

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            _cv.value = getCv(id)
        }
    }

    init {
        val initialInfo = getApp().getString(R.string.initial_info)
        _cv.value = CV(initialInfo)
//        getApp().registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
//        viewModelScope.launch(Dispatchers.IO) {
//            cvDataSource = CvDataSource(getDownloadManager())
//        }

        getCvAsString()
    }

    private fun getCvAsString() {
        CvApi.retrofitService.getCv().enqueue( object: Callback<List<CvItem>> {
            override fun onFailure(call: Call<List<CvItem>>, t: Throwable) {
                //_response.value = "Failure: " + t.message
                _cv.value = CV("Failure: " + t.message)
            }

            override fun onResponse(call: Call<List<CvItem>>, response: Response<List<CvItem>>) {
                //_response.value = "Success: " + response.body()
                _cv.value = CV("Success: " + response.body()?.size)
            }
        })
    }

    private fun getApp() = getApplication<Application>()

    private fun getDownloadManager() =
        getApp().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun getCv(id: Long): CV {
        return if (id == cvDataSource.downloadId) {
            cvDataSource.getCvContent(getDownloadManager(), getApp().contentResolver)
        } else {
            val infoAboutDownloadFailure =
                getApp().getString(R.string.info_about_failed_download_of_cv)
            CV(infoAboutDownloadFailure)
        }
    }
}