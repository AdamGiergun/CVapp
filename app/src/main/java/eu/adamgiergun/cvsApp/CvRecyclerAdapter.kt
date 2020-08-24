package eu.adamgiergun.cvsApp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eu.adamgiergun.cvsApp.databinding.CvItemBinding

internal class CvRecyclerAdapter: ListAdapter<CvItem, CvRecyclerAdapter.ViewHolder>(CvItemDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cvItem = getItem(position)
        holder.bind(cvItem)
    }

    internal class ViewHolder private constructor(private val binding: CvItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CvItem) {
            binding.cvItem = item
            binding.executePendingBindings()
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