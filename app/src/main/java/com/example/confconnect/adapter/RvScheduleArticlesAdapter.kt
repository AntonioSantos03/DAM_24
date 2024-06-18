package com.example.confconnect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.Articles
import com.example.confconnect.R
import com.example.confconnect.databinding.ScheduleArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleArticlesAdapter(private val context: Context, private var articlesList: ArrayList<Articles>) :
    RecyclerView.Adapter<ScheduleArticlesAdapter.ViewHolder>() {

    private var onItemClicked: (Articles) -> Unit = {}

    inner class ViewHolder(val binding: ScheduleArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ScheduleArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = articlesList[position]
        holder.apply {
            binding.apply {
                tvTitle.text = currentItem.title
                tvDate.text = currentItem.date
                tvTime.text = currentItem.time
                tvRoom.text = currentItem.room

                // Set background color based on the day of the week
                val dayOfWeek = getDayOfWeek(currentItem.date)
                val color = getColorForDay(dayOfWeek)
                leftBar.setBackgroundColor(color)

                root.setOnClickListener {
                    onItemClicked(currentItem)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Articles) -> Unit) {
        onItemClicked = listener
    }

    fun updateList(newList: ArrayList<Articles>) {
        articlesList = newList
        notifyDataSetChanged()
    }

    // Get the day of the week from the date
    private fun getDayOfWeek(dateString: String?): Int {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            val date = format.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.get(Calendar.DAY_OF_WEEK)
        } catch (e: Exception) {
            -1
        }
    }

    // Get color based on the day of the week from resources
    private fun getColorForDay(dayOfWeek: Int): Int {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> ContextCompat.getColor(context, R.color.yellow)
            Calendar.MONDAY -> ContextCompat.getColor(context, R.color.orange)
            Calendar.TUESDAY -> ContextCompat.getColor(context, R.color.red2)
            Calendar.WEDNESDAY -> ContextCompat.getColor(context, R.color.pink)
            Calendar.THURSDAY -> ContextCompat.getColor(context, R.color.lightblue)
            Calendar.FRIDAY -> ContextCompat.getColor(context, R.color.green)
            Calendar.SATURDAY -> ContextCompat.getColor(context, R.color.softpurple)
            else -> ContextCompat.getColor(context, R.color.white)
        }
    }
}
