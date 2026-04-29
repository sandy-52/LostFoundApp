package com.example.lostfoundapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundapp.databinding.ActivityPostDetailBinding
import android.content.Intent
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

        val type = intent.getStringExtra("type") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val imageUri = intent.getStringExtra("imageUri") ?: ""
        val timestamp = intent.getStringExtra("timestamp") ?: ""

        // ✅ Safe image loading
        if (imageUri.isNotEmpty()) {
            try {
                val uri = Uri.parse(imageUri)
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                binding.detailImage.setImageURI(uri)
            } catch (e: Exception) {
                binding.detailImage.setImageResource(android.R.drawable.ic_menu_gallery)
                Toast.makeText(this, "Image could not be loaded", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.detailImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

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
            if (postId == -1) {
                Toast.makeText(this, "Invalid advert ID", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val deleted = db.deletePost(postId)

            if (deleted) {
                Toast.makeText(this, "Advert removed successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show()
            }
        }
    }
}