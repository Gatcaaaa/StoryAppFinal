package com.submisson.aleggappstory.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submisson.aleggappstory.data.response.ListStoryItem
import com.submisson.aleggappstory.databinding.ItemLayoutBinding
import com.submisson.aleggappstory.view.detail.DetailActivity

class ListAdapter: PagingDataAdapter<ListStoryItem, ListAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStoryItem: ListStoryItem) {
            binding.tvName.text = listStoryItem.name
            binding.tvDescription.text = listStoryItem.description

            Glide
                .with(itemView.context)
                .load(listStoryItem.photoUrl)
                .fitCenter()
                .into(binding.ivStoryImage)

            binding.itemLayout.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("storyItem", listStoryItem)

                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivStoryImage, "story_image"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDescription, "description")
                    )
                itemView.context.startActivity(intent, optionCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bind(item)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}