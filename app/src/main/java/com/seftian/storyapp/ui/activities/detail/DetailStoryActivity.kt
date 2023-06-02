package com.seftian.storyapp.ui.activities.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.seftian.storyapp.R
import com.seftian.storyapp.databinding.ActivityDetailStoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailStoryBinding
    private val viewModel: DetailStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shapeToolbar()
        playAnimation()

        binding.topAppBar.setNavigationOnClickListener{
            finish()
        }

        val receivedId = intent.getStringExtra("storyId")

        receivedId?.let { viewModel.getStory(it) }

        viewModel.userStory.observe(this){story->
            Glide
                .with(this)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_person)
                .into(binding.ivDetail)

            binding.tvTitle.text = resources.getString(R.string.story_by, story.name)
            binding.tvDescription.text = story.description
        }
    }


    private fun shapeToolbar(){

        val radius = resources.getDimension(R.dimen.corner_size)
        val toolbar = binding.topAppBar


        val materialShapeDrawable = toolbar.background as MaterialShapeDrawable
        materialShapeDrawable.shapeAppearanceModel = materialShapeDrawable.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()
    }


    private fun playAnimation(){
        val storyImage = ObjectAnimator.ofFloat(binding.ivDetail, View.ALPHA, 1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1F).setDuration(500)
        val description= ObjectAnimator.ofFloat(binding.tvDescription, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(storyImage, title, description)
            start()
        }
    }
}