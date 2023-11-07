package com.submisson.aleggappstory.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.submisson.aleggappstory.R
import com.submisson.aleggappstory.databinding.ActivityMainBinding
import com.submisson.aleggappstory.view.ViewModelFactory
import com.submisson.aleggappstory.view.maps.MapsActivity
import com.submisson.aleggappstory.view.upload.AddStoryActivity
import com.submisson.aleggappstory.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(true)

        val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryList.layoutManager = layoutManager

        mainViewModel.getSession().observe(this){ result ->
            val token = result.token
            if (!result.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            setupListItem(token)
        }

        setupAddStory()
    }

    private fun setupAddStory() {
        binding.addStory.setOnClickListener{
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupListItem(token: String) {
        val adapter = ListAdapter()
        binding.rvStoryList.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.getStories(token).observe(this){
            adapter.submitData(lifecycle, it)
        }
        showLoading(false)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater  = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> mainViewModel.logout()
            R.id.menu_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.rvStoryList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}