package eu.adamgiergun.cvsApp

import android.app.DownloadManager
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import kotlin.properties.Delegates

class CvDataSource(downloadManager: DownloadManager) {

    internal var downloadId by Delegates.notNull<Long>()

    init {
        startDownloadingCV(downloadManager)
    }

    private fun getDownloadManagerCursor(downloadManager: DownloadManager): Cursor {
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

    private fun startDownloadingCV(downloadManager: DownloadManager) {
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
    }

    internal fun getCvContent(
        downloadManager: DownloadManager,
        contentResolver: ContentResolver
    ): CV {
        val cv = CV()
        getDownloadManagerCursor(downloadManager).use { cursor ->
            if (cursor.moveToFirst()) {
                if (isDownloadStatusSuccessful(cursor)) {

                    fun getInputStream(): InputStream? {
                        fun getLocalUri(): Uri {
                            fun Cursor.getLocalUriString(): String {
                                val localUriColumnIndex =
                                    this.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                return this.getString(localUriColumnIndex)
                            }
                            return Uri.parse(cursor.getLocalUriString())
                        }
                        return contentResolver
                            .openInputStream(getLocalUri())
                    }

                    getInputStream().use { inputStream ->
                        val myParser = XmlPullParserFactory.newInstance().newPullParser()
                        myParser.setInput(inputStream, null)

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
        }
        return cv
    }
}