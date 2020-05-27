package eu.adamgiergun.cvsApp

class CV constructor(): ArrayList<CvItem>() {
    constructor(info: String) : this() {
        this.add(CvItem(false, info))
    }
}