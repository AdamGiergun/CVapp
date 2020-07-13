package eu.adamgiergun.cvsApp

import android.graphics.Typeface
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

internal class CvRecyclerAdapter (private val cv: CV) :
    RecyclerView.Adapter<CvRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cv_item, parent, false) as CardView
        return ViewHolder(cardView)
    }

    override fun getItemCount() = cv.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cvItem = cv.get(position)
        holder.bind(cvItem)
    }

    internal class ViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        fun bind(cvItem: CvItem) {
            val itemTextView = itemView.findViewById<TextView>(R.id.itemTextView)
            itemTextView.text = cvItem.text

            val color: Int
            val resources = itemView.context.resources
            color = if (cvItem.isHeader) {
                itemTextView.setTypeface(null, Typeface.BOLD)
                resources.getColor(R.color.colorHeader, null)
            } else {
                itemTextView.setTypeface(null, Typeface.NORMAL)
                resources.getColor(R.color.colorItem, null)
            }
            val cardView = itemView as CardView
            cardView.setCardBackgroundColor(color)
        }
    }
}