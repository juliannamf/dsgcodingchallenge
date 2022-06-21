package com.example.dsgcodingchallenge

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import java.util.concurrent.Executors


class SearchResultAdapter(private val dataSet: ArrayList<Event>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
        val eventTitle: TextView = view.findViewById(R.id.title)
        val eventLocation: TextView = view.findViewById(R.id.location)
        val eventDateAndTime: TextView = view.findViewById(R.id.dateandtime)
        val eventImage: ShapeableImageView = view.findViewById(R.id.eventImage)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)

        init {

            view.setOnClickListener {

                listener.onItemClick(adapterPosition)

            }

        }
    }

    // Create new views
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_result_list, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    // Replace the contents of a view
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.eventTitle.text = dataSet[position].title
        viewHolder.eventLocation.text = dataSet[position].location
        viewHolder.eventDateAndTime.text = dataSet[position].dateandtime

        if (dataSet[position].isFavorite) {
            viewHolder.favoriteIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else{
            viewHolder.favoriteIcon.setImageResource(0)
        }

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null

        executor.execute {

            val image_url = dataSet[position].image_url
            try {
                val `in` = java.net.URL(image_url).openStream()
                image = BitmapFactory.decodeStream(`in`)

                handler.post {
                    viewHolder.eventImage.setImageBitmap(image)
                }
            }

            catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    // Return the size of the dataset
    override fun getItemCount() = dataSet.size

    // Add item to list
    fun addItem(event: Event) {
        dataSet.add(event)
        notifyItemInserted(itemCount-1)
    }

    // Clear the list of all elements
    fun clearList() {
        val size = dataSet.size
        if (size > 0) {
            for (i in 0 until size) {
                dataSet.removeAt(0)
            }
            notifyItemRangeRemoved(0, size)
        }
    }

}
