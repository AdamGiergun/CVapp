package eu.adamgiergun.cvsApp

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

internal class ViewModel(application: Application) : AndroidViewModel(application) {

    private val cvDataSource: CvDataSource

    private val _cv = MutableLiveData<CV>()
    val cv: LiveData<CV>
        get() = _cv

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            _cv.value = getCv(id)
        }
    }

    init {
        val initialInfo = getApp().getString(R.string.initial_info)
        _cv.value = CV(initialInfo)
        getApp().registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        cvDataSource = CvDataSource(getDownloadManager())
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