package eu.adamgiergun.cvsApp

import android.app.*
import android.content.Context
import androidx.lifecycle.AndroidViewModel

internal class ViewModel(application: Application) : AndroidViewModel(application) {

    private val cvSource: CvSource

    init {
        cvSource = CvSource(getDownloadManager())
    }

    private fun getApp() = getApplication<Application>()

    private fun getDownloadManager() =
        getApp().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun getCv(id: Long): CV {
        return if (id == cvSource.downloadId) {
            cvSource.getCvContent(getDownloadManager(), getApp().contentResolver)
        } else {
            val infoAboutDownloadFailure =
                getApp().getString(R.string.info_about_failed_download_of_cv)
            CV(infoAboutDownloadFailure)
        }
    }

    internal fun getCvRecyclerAdapter(id: Long): CvRecyclerAdapter {
        return CvRecyclerAdapter(getCv(id))
    }
}