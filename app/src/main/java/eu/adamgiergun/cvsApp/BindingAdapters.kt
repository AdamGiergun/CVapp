package eu.adamgiergun.cvsApp

import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("listData")
internal fun bindRecyclerView(recyclerView: RecyclerView, data: List<CvItem>?) {
    val adapter = recyclerView.adapter as CvRecyclerAdapter
    adapter.submitList(data)
}

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

@BindingAdapter("cvApiStatus")
fun bindStatus(statusImageView: ImageView, status: CvApiStatus?) {
    when (status) {
        CvApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        CvApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        CvApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}