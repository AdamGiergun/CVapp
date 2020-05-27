package eu.adamgiergun.cvsApp

import android.app.*
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import org.xmlpull.v1.*
import java.io.InputStream
import kotlin.properties.Delegates

class ViewModel(application: Application) : AndroidViewModel(application) {
    private var downloadId by Delegates.notNull<Long>()

    fun startDownloadingCV() {
        val downloadManager = getApplication<Application>().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadCV(downloadManager)
    }

    private fun downloadCV(downloadManager: DownloadManager): Long {
        val url =
            "https://gist.githubusercontent.com/AdamGiergun/10d8f582d6cca24b37eb4af396288bad/raw/896585ba9d125b17558e563f8805aa1c4120e434/cvs.xml"
        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setMimeType("application/xml")

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)

        startDownloadInThread(downloadManager, query)
        return downloadId
    }

    private fun startDownloadInThread(downloadManager: DownloadManager, query: DownloadManager.Query) {
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                cursor.close()
            }
        }).start()
    }

    fun onBroadcastReceive(id: Long): CV {
        return if (downloadId == id) {
            getCvContent(id)
        } else {
            CV(getApplication<Application>().getString(R.string.info_about_failed_download_of_cv))
        }
    }

    private fun getCvContent(id: Long): CV {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val downloadManager =
            getApplication<Application>().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return getCvContentFromCursor(downloadManager.query(query))
    }

    private fun getCvContentFromCursor(cursor: Cursor): CV {
        var content = CV()
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val localURI =
                    Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                val input: InputStream? =
                    getApplication<Application>().contentResolver.openInputStream(localURI)
                content = parseInputStream(input)
            }
        }
        return content
    }

    private fun parseInputStream(input: InputStream?): CV {
        val content = CV()
        val xmlFactoryObject = XmlPullParserFactory.newInstance()
        val myParser = xmlFactoryObject.newPullParser()
        myParser.setInput(input, null)

        var event = myParser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    content.add(CvItem(true,myParser.name.replace("_", " ")))
                }
                XmlPullParser.TEXT -> {
                    content.add(CvItem(false, myParser.text))
                }
            }
            event = myParser.next()
        }
        return content
    }
}