package eu.adamgiergun.cvsApp

import android.app.*
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import org.xmlpull.v1.*
import java.io.InputStream
import kotlin.properties.Delegates

internal class ViewModel(application: Application) : AndroidViewModel(application) {

    private var downloadId by Delegates.notNull<Long>()

    private fun getApp() = getApplication<Application>()

    private fun getDownloadManager() = getApp().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun getDownloadManagerCursor(): Cursor {
        val query =
            DownloadManager.Query()
                .setFilterById(downloadId)
        return getDownloadManager().query(query)
    }

    private fun isDownloadStatusSuccessful(cursor: Cursor): Boolean {
        val indexOfDownloadStatusColumn =
            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val downloadStatus = cursor.getInt(indexOfDownloadStatusColumn)
        return downloadStatus == DownloadManager.STATUS_SUCCESSFUL
    }

    internal fun startDownloadingCV() {
        val url =
            "https://gist.githubusercontent.com/AdamGiergun/10d8f582d6cca24b37eb4af396288bad/raw/896585ba9d125b17558e563f8805aa1c4120e434/cvs.xml"
        val downloadUri = Uri.parse(url)
        val request =
            DownloadManager.Request(downloadUri)
                .apply {
                    setAllowedOverMetered(true)
                    setAllowedOverRoaming(true)
                    setMimeType("application/xml")
                }

        downloadId = getDownloadManager().enqueue(request)

        Thread(Runnable {
            var isDownloading = true
            while (isDownloading) {
                getDownloadManagerCursor().use {
                    if (it.moveToFirst()) {
                        if (isDownloadStatusSuccessful(it)) {
                            isDownloading = false
                        }
                    } else {
                        isDownloading = false
                    }
                }
            }
        }).start()
    }

    internal fun getCv(id: Long): CV {
        fun getCvContent(): CV {
            val cv = CV()
            getDownloadManagerCursor().use {
                if (it.moveToFirst()) {
                    if (isDownloadStatusSuccessful(it)) {
                        fun getInputStream(): InputStream? {
                            fun getLocalUri() : Uri {
                                fun Cursor.getLocalUriString(): String {
                                    val localUriColumnIndex = this.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                    return this.getString(localUriColumnIndex)
                                }
                                return Uri.parse(it.getLocalUriString())
                            }
                            return getApp().contentResolver
                                .openInputStream(getLocalUri())
                        }

                        val myParser = XmlPullParserFactory.newInstance().newPullParser()
                        myParser.setInput(getInputStream(), null)

                        var event = myParser.eventType
                        while (event != XmlPullParser.END_DOCUMENT) {
                            when (event) {
                                XmlPullParser.START_TAG ->
                                    cv.add(CvItem(true, myParser.name.replace("_", " ")))
                                XmlPullParser.TEXT ->
                                    cv.add(CvItem(false, myParser.text))
                            }
                            event = myParser.next()
                        }
                    }
                }
            }
            return cv
        }

        return if (downloadId == id) {
            getCvContent()
        } else {
            val infoAboutDownloadFailure=  getApp().getString(R.string.info_about_failed_download_of_cv)
            CV(infoAboutDownloadFailure)
        }
    }
}