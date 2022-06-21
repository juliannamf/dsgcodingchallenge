package com.example.dsgcodingchallenge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.util.concurrent.Executors

class EventDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        // Set back button operation
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        // Get text views from layout
        val eventTitle = findViewById<TextView>(R.id.eventTitle)
        val eventLocation = findViewById<TextView>(R.id.eventLocation)
        val eventDateAndTime = findViewById<TextView>(R.id.eventDateAndTime)
        val eventImage = findViewById<ImageView>(R.id.eventImage)
        val favoriteBtn = findViewById<ImageView>(R.id.favoriteBtn)

        // Get event information from intent
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val location = intent.getStringExtra("location")
        val dateandtime = intent.getStringExtra("dateandtime")
        val image_url = intent.getStringExtra("image_url")
        var isFavorite = intent.getBooleanExtra("isFavorite", false)

        // Put strings into text views
        eventTitle.text = title
        eventLocation.text = location
        eventDateAndTime.text = dateandtime
        if (isFavorite) {
            favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        // Load image into Image View
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap?
        executor.execute {
            try {
                val `in` = java.net.URL(image_url).openStream()
                image = BitmapFactory.decodeStream(`in`)

                handler.post {
                    eventImage.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        // Create Shared Preference file
        val pref = getSharedPreferences("favorites", MODE_PRIVATE)
        val editor = pref.edit()

        // Handle favorite action
        favoriteBtn.setOnClickListener {
            if (!isFavorite) {
                val gson = Gson()
                if (id != null && title != null && location != null && dateandtime != null && image_url != null) {
                    val list: List<String> = listOf(id, title, location, dateandtime, image_url)
                    val value = gson.toJson(list)
                    if (!pref.contains(id)) {
                        editor.putString(id, value)
                        editor.apply()
                    }
                }
                isFavorite = true
                favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
            } else if (isFavorite) {
                isFavorite = false
                editor.remove(id)
                editor.apply()
                favoriteBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }
    }
}