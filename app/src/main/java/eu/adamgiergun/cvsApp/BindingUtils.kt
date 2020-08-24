package eu.adamgiergun.cvsApp

import android.graphics.Typeface
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter

@BindingAdapter("cvItemTextStyle")
internal fun TextView.setCvItemStyling(item: CvItem?) {
    item?.let {
        if (it.isHeader) {
            this.setTypeface(this.typeface, Typeface.BOLD)
        } else {
            this.typeface = Typeface.create(this.typeface, Typeface.NORMAL)
        }
    }
}

@BindingAdapter("cvItemCardColor")
internal fun CardView.setCvItemCardBackgroundColor(item: CvItem?) {
    item?.let {
        val resources = this.context.resources
        val color = resources.getColor(
            if (it.isHeader) {R.color.colorHeader} else { R.color.colorItem},
            null)
        this.setCardBackgroundColor(color)
    }
}