package com.example.ones.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ones.R
import com.example.ones.data.remote.response.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val articles = mutableListOf<Article>()

    fun submitList(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivNewsImage: ImageView = itemView.findViewById(R.id.ivNewsImage)
        private val tvNewsTitle: TextView = itemView.findViewById(R.id.tvNewsTitle)
        private val tvNewsDescription: TextView = itemView.findViewById(R.id.tvNewsDescription)

        fun bind(article: Article) {
            tvNewsTitle.text = article.title
            tvNewsDescription.text = article.description ?: "No description available"

            // Load image with Glide
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_placeholder)
                .into(ivNewsImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size
}