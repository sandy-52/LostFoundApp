package com.example.lostfoundapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundapp.databinding.ActivityPostDetailBinding

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private lateinit var db: DatabaseHelper
    private var postId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        postId = intent.getIntExtra("id", -1)
        val type = intent.getStringExtra("type")
        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val location = intent.getStringExtra("location")
        val category = intent.getStringExtra("category")
        val imageUri = intent.getStringExtra("imageUri")
        val timestamp = intent.getStringExtra("timestamp")

        binding.detailImage.setImageURI(Uri.parse(imageUri))

        binding.tvDetails.text = """
            Type: $type
            Name: $name
            Phone: $phone
            Description: $description
            Date: $date
            Location: $location
            Category: $category
            Posted: $timestamp
        """.trimIndent()

        binding.btnRemove.setOnClickListener {
            if (db.deletePost(postId)) {
                Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error removing advert", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
