package eu.adamgiergun.cvsApp

internal class CV constructor(): ArrayList<CvItem>() {
    internal constructor(info: String) : this() {
        this.add(CvItem(false, info))
    }
}