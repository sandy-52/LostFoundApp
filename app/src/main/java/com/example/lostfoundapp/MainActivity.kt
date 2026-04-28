package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreate.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        binding.btnShow.setOnClickListener {
            startActivity(Intent(this, PostListActivity::class.java))
        }
    }
}