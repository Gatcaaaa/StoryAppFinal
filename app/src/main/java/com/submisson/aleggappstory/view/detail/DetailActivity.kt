package com.submisson.aleggappstory.view.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.submisson.aleggappstory.R
import com.submisson.aleggappstory.data.response.ListStoryItem
import com.submisson.aleggappstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var listStoryItem: ListStoryItem
    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listStoryItem = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("storyItem", ListStoryItem::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("storyItem")!!
        }

        setDetail()
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setDetail() {
        Glide
            .with(this)
            .load(listStoryItem.photoUrl)
            .fitCenter()
            .into(binding.ivProfilePhoto)

        binding.tvName.text = listStoryItem.name
        binding.tvDescription.text = listStoryItem.description
    }
}