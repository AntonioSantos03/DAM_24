package com.example.confconnect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.confconnect.Articles
import com.example.confconnect.databinding.RvArticlesBinding


class RvArticlesAdapter(private val articlesList: java.util.ArrayList<Articles>) : RecyclerView.Adapter<RvArticlesAdapter.ViewHolder>() {
    class ViewHolder(val binding: RvArticlesBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = articlesList[position]
        holder.apply {
            binding.apply {
                binding.tvTitle.text = currentItem.title
                binding.tvAuthor.text = currentItem.author
                binding.tvDate.text = currentItem.date
                binding.tvDescription.text = currentItem.description
            }
        }
    }
}