package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundapp.databinding.ActivityPostListBinding

class PostListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostListBinding
    private lateinit var db: DatabaseHelper
    private lateinit var posts: ArrayList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        val filters = arrayOf("All", "Electronics", "Pets", "Wallets", "Keys", "Clothing", "Other")
        binding.spinnerFilter.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filters)

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = filters[position]
                posts = if (category == "All") db.getAllPosts() else db.getPostsByCategory(category)

                val titles = posts.map {
                    "${it.type}: ${it.description}\nCategory: ${it.category}\nPosted: ${it.timestamp}"
                }

                binding.listViewPosts.adapter =
                    ArrayAdapter(this@PostListActivity, android.R.layout.simple_list_item_1, titles)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.listViewPosts.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("id", posts[position].id)
            intent.putExtra("type", posts[position].type)
            intent.putExtra("name", posts[position].name)
            intent.putExtra("phone", posts[position].phone)
            intent.putExtra("description", posts[position].description)
            intent.putExtra("date", posts[position].date)
            intent.putExtra("location", posts[position].location)
            intent.putExtra("category", posts[position].category)
            intent.putExtra("imageUri", posts[position].imageUri)
            intent.putExtra("timestamp", posts[position].timestamp)
            startActivity(intent)
        }
    }
}

