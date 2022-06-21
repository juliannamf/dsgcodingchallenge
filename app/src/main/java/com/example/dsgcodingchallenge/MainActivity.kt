package com.example.dsgcodingchallenge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    // Declare variables
    private lateinit var searchBar: EditText
    private lateinit var recyclerViewEvents: RecyclerView
    private lateinit var searchResultAdapter: SearchResultAdapter

    // Initialize search results list as empty array list
    private var searchResultsList = arrayListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize variables for views
        searchBar = findViewById(R.id.search_bar)
        recyclerViewEvents = findViewById(R.id.search_results)

        // Create Text Watcher for search bar
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                searchBar.addTextChangedListener(textWatcher)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Handle cancel search bar click
        val canceltv = findViewById<TextView>(R.id.cancel)
        canceltv.setOnClickListener {
            searchBar.text = null
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = currentFocus
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }

        // Create layout and adapter for recycler view
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        searchResultAdapter = SearchResultAdapter(searchResultsList)
        recyclerViewEvents.adapter = searchResultAdapter

        // Send event details to EventDetailsActivity when a row is clicked on
        searchResultAdapter.setOnItemClickListener(object: SearchResultAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, EventDetailsActivity::class.java)

                // Send event information in intent
                intent.putExtra("title", searchResultsList[position].title)
                intent.putExtra("location", searchResultsList[position].location)
                intent.putExtra("dateandtime", searchResultsList[position].dateandtime)
                intent.putExtra("image_url", searchResultsList[position].image_url)

                // Send user to Event Details Activity
                startActivity(intent)
            }
        })

        // Initialize search results
        val client_id = getString(R.string.client_id)
        val url = "https://api.seatgeek.com/2/events?client_id=$client_id&q=swift"
        getSearchResults(url)

    }

    // Text Watcher for search bar to instantly update search results
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val client_id = getString(R.string.client_id)
            val searchQuery = s.toString()
            val url = "https://api.seatgeek.com/2/events?client_id=$client_id&q=$searchQuery"
            getSearchResults(url)

        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun getSearchResults(url: String) {
        try {
            searchResultAdapter.clearList()
            val queue = Volley.newRequestQueue(applicationContext)
            val request = StringRequest(Request.Method.GET, url,
                { response ->

                    // Convert response into JSON Object
                    val data = response.toString()
                    val jsonArray = JSONObject(data).getJSONArray("events")

                    // Iterate through events and add to recycler view
                    for (i in 0 until jsonArray.length()) {
                        val event = jsonArray.getJSONObject(i)
                        val title = event.getString("short_title")
                        val city = event.getJSONObject("venue").getString("city")
                        val state = event.getJSONObject("venue").getString("state")
                        val dateandtime = event.getString("datetime_local")
                        val image_url = event.getJSONArray("performers").getJSONObject(0).getString("image")

                        // Format Location
                        val location = "$city, $state"

                        // Format date and time
                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-dd-MM'T'HH:mm:ss")
                        val date: Date = dateFormat.parse(dateandtime)
                        val newDateFormat: DateFormat = SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa")
                        val newdateandtime = newDateFormat.format(date)

                        // Add event to recyclerview
                        searchResultAdapter.addItem(Event(title, location, newdateandtime, image_url))
                    }
            }, { error -> error.printStackTrace() })
        queue.add(request)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}