package eu.adamgiergun.cvsApp

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou

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
            if (it.isHeader) {
                R.color.colorHeader
            } else {
                R.color.colorItem
            },
            null
        )
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

@BindingAdapter("imageUrl", "imageLink")
fun bindImage(imgView: ImageView, imgUrl: String?, imgLnk: String?) {
    if (imgUrl == "")
    {
        imgView.setOnClickListener {  }
        imgView.visibility = ImageView.GONE
    } else {
        imgView.visibility = ImageView.VISIBLE

        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            if (imgUrl.endsWith(".svg")) {
                GlideToVectorYou.init().with(imgView.context)
                    .setPlaceHolder(R.drawable.loading_animation, R.drawable.ic_broken_image)
                    .load(imgUri, imgView)
            } else {
                Glide.with(imgView.context)
                    .load(imgUri)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                    )
                    .into(imgView)
            }
            if (imgLnk != "") {
                imgLnk?.let {
                    val imgLnkUri = imgLnk.toUri().buildUpon().scheme("http").build()
                    imgView.setOnClickListener {
                        val webLink = Intent(Intent.ACTION_VIEW)
                        webLink.data = imgLnkUri
                        imgView.context.startActivity(webLink)
                    }
                }
            }
        }


    }
}