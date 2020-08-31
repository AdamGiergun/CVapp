package eu.adamgiergun.cvsApp

import com.squareup.moshi.Json

internal data class CvItem (
    val text : String,
    val isHeader : Boolean,
    @Json(name="img_src") val imgSrcUrl: String,
    @Json(name="img_lnk") val imgLinkUrl: String)