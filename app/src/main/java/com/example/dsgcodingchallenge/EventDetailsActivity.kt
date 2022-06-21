package com.example.dsgcodingchallenge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

        // Get event information from intent
        val title = intent.getStringExtra("title")
        val location = intent.getStringExtra("location")
        val dateandtime = intent.getStringExtra("dateandtime")
        val image_url = intent.getStringExtra("image_url")

        // Put strings into text views
        eventTitle.text = title
        eventLocation.text = location
        eventDateAndTime.text = dateandtime

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
    }
}