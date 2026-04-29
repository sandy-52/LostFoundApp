package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundapp.databinding.ActivityPostListBinding
import android.widget.Toast

class PostListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostListBinding
    private lateinit var db: DatabaseHelper
    private lateinit var posts: ArrayList<Post>

    private val filters = arrayOf("All", "Electronics", "Pets", "Wallets", "Keys", "Clothing", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.spinnerFilter.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filters)

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadPosts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.listViewPosts.setOnItemClickListener { _, _, position, _ ->

            Toast.makeText(this, "Item clicked", Toast.LENGTH_SHORT).show()

            val post = posts[position]

            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("id", post.id)
            intent.putExtra("type", post.type)
            intent.putExtra("name", post.name)
            intent.putExtra("phone", post.phone)
            intent.putExtra("description", post.description)
            intent.putExtra("date", post.date)
            intent.putExtra("location", post.location)
            intent.putExtra("category", post.category)
            intent.putExtra("imageUri", post.imageUri)
            intent.putExtra("timestamp", post.timestamp)

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }

    private fun loadPosts() {
        val selectedCategory = binding.spinnerFilter.selectedItem.toString()

        posts = if (selectedCategory == "All") {
            db.getAllPosts()
        } else {
            db.getPostsByCategory(selectedCategory)
        }

        val titles = posts.map {
            "${it.type}: ${it.description}\nCategory: ${it.category}\nPosted: ${it.timestamp}"
        }

        binding.listViewPosts.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)
    }
}
