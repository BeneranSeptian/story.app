package com.seftian.storyapp.ui.activities.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seftian.storyapp.R
import com.seftian.storyapp.databinding.StoryItemBinding
import com.seftian.storyapp.domain.Story

class StoryAdapter(
    private var storyList: List<Story>,
    private val context: Context,
    private val itemClickListener: StoryAdapterClickListener
): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

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

        val story = storyList[position]

        val stringSource = context.getString(R.string.story_by)
        val formattedString = String.format(stringSource, story.name)

        holder.binding.apply {

            Glide
                .with(holder.itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_person)
                .into(ivStory)

            tvStory.text = formattedString
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(story.id)
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    fun updateStoryList(newList: List<Story>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = storyList.size
            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                storyList[oldItemPosition] == newList[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                storyList[oldItemPosition] == newList[newItemPosition]
        })

        storyList = newList
        diffResult.dispatchUpdatesTo(this)
    }
}