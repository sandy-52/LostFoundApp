package com.example.lostfoundapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "lost_found.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE posts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT,
                name TEXT,
                phone TEXT,
                description TEXT,
                date TEXT,
                location TEXT,
                category TEXT,
                imageUri TEXT,
                timestamp TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS posts")
        onCreate(db)
    }

    fun insertPost(post: Post): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("type", post.type)
            put("name", post.name)
            put("phone", post.phone)
            put("description", post.description)
            put("date", post.date)
            put("location", post.location)
            put("category", post.category)
            put("imageUri", post.imageUri)
            put("timestamp", post.timestamp)
        }

        val result = db.insert("posts", null, values)
        db.close()

        return result != -1L
    }

    fun getAllPosts(): ArrayList<Post> {
        val posts = ArrayList<Post>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM posts ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                )

                posts.add(post)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return posts
    }

    fun getPostsByCategory(category: String): ArrayList<Post> {
        val posts = ArrayList<Post>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM posts WHERE category = ? ORDER BY id DESC",
            arrayOf(category)
        )

        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                )

                posts.add(post)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return posts
    }

    fun deletePost(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("posts", "id = ?", arrayOf(id.toString()))
        db.close()

        return result > 0
    }
}