package com.example.lostfoundapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "lost_found.db", null, 1) {

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
            """
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
        return result != -1L
    }

    fun getAllPosts(): ArrayList<Post> {
        val posts = ArrayList<Post>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM posts ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                posts.add(
                    Post(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
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
                posts.add(
                    Post(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return posts
    }

    fun deletePost(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("posts", "id=?", arrayOf(id.toString()))
        return result > 0
    }
}
