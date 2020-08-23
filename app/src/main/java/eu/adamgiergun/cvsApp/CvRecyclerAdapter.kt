package eu.adamgiergun.cvsApp

import android.graphics.Typeface
import android.view.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.adamgiergun.cvsApp.databinding.CvItemBinding

internal class CvRecyclerAdapter (private val cv: CV): ListAdapter<CvItem, CvRecyclerAdapter.ViewHolder>(CvItemDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = cv.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cvItem = cv[position]
        holder.bind(cvItem)
    }

    internal class ViewHolder private constructor(private val binding: CvItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cvItem: CvItem) {
            val itemTextView = binding.itemTextView
            itemTextView.text = cvItem.text

            val color: Int
            val resources = itemView.context.resources
            color = if (cvItem.isHeader) {
                itemTextView.setTypeface(itemTextView.typeface, Typeface.BOLD)
                resources.getColor(R.color.colorHeader, null)

            } else {
                itemTextView.typeface = Typeface.create(itemTextView.typeface, Typeface.NORMAL)
                resources.getColor(R.color.colorItem, null)
            }
            val cardView = itemView as CardView
            cardView.setCardBackgroundColor(color)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CvItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

internal class CvItemDiffCallback: DiffUtil.ItemCallback<CvItem>() {
    override fun areItemsTheSame(oldItem: CvItem, newItem: CvItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CvItem, newItem: CvItem): Boolean {
        return oldItem == newItem
    }
}