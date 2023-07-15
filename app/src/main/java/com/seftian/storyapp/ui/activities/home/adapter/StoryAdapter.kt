package com.seftian.storyapp.ui.activities.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seftian.storyapp.R
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.databinding.StoryItemBinding

class StoryAdapter(
    private val context: Context,
    private val itemClickListener: StoryAdapterClickListener
): PagingDataAdapter<StoryResponse,StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder( val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        return ViewHolder(StoryItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
            false
            ))
    }

    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {

        val story = getItem(position)

        val stringSource = context.getString(R.string.story_by)
        val formattedString = String.format(stringSource, story?.name)

        holder.binding.apply {

            Glide
                .with(holder.itemView.context)
                .load(story?.photoUrl)
                .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
                .into(ivStory)

            tvStory.text = formattedString
        }

        holder.itemView.setOnClickListener {
            if (story != null) {
                itemClickListener.onItemClick(story.id)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse>() {

            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryResponse,
                newItem: StoryResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}