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

    private val downloadManager =
        getApplication<Application>()
            .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private fun getDownloadManagerCursor(): Cursor {
        val query =
            DownloadManager.Query()
                .setFilterById(downloadId)
        return downloadManager.query(query)
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

        downloadId = downloadManager.enqueue(request)

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

    internal fun onBroadcastReceive(id: Long): CV {

        fun getCvContent(): CV {
            val cursor = getDownloadManagerCursor()
            val cv = CV()

            if (cursor.moveToFirst()) {
                if (isDownloadStatusSuccessful(cursor)) {
                    fun getParser(): XmlPullParser {
                        val xmlFactoryObject = XmlPullParserFactory.newInstance()
                        return xmlFactoryObject.newPullParser()
                    }

                    val myParser = getParser()

                    fun getInputStream(): InputStream? {
                        val localURI =
                            Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                        return getApplication<Application>().contentResolver.openInputStream(
                            localURI
                        )
                    }
                    myParser.setInput(getInputStream(), null)

                    var event = myParser.eventType
                    while (event != XmlPullParser.END_DOCUMENT) {
                        when (event) {
                            XmlPullParser.START_TAG -> {
                                cv.add(CvItem(true, myParser.name.replace("_", " ")))
                            }
                            XmlPullParser.TEXT -> {
                                cv.add(CvItem(false, myParser.text))
                            }
                        }
                        event = myParser.next()
                    }
                }
            }
            return cv
        }

        return if (downloadId == id) {
            getCvContent()
        } else {
            fun getInfoAboutFailure(): String {
                val application = getApplication<Application>()
                return application.getString(R.string.info_about_failed_download_of_cv)
            }
            CV(getInfoAboutFailure())
        }
    }
}