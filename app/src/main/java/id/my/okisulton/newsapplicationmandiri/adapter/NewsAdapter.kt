package id.my.okisulton.newsapplicationmandiri.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.my.okisulton.newsapplicationmandiri.R
import id.my.okisulton.newsapplicationmandiri.databinding.ItemNewsBinding
import id.my.okisulton.newsapplicationmandiri.network.model.NewsResponse
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Oki Sulton on 03/08/2022.
 */
class NewsAdapter(
    private val listNews: ArrayList<NewsResponse.ArticlesItem>,
    private val listener: OnAdapterListener
): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    interface OnAdapterListener {
        fun onClick(result: NewsResponse.ArticlesItem)
    }
    class ViewHolder (
        val binding: ItemNewsBinding
        ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listNews[position]

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm")
        val date: Date = inputFormat.parse(data.publishedAt.toString())!!
        val formattedDate: String = outputFormat.format(date)


        holder.binding.apply {
            tvTitle.text = data.title
            tvDescription.text = data.description
            tvSource.text = data.source?.name
            tvPublishedAt.text = formattedDate

            Glide.with(root)
                .load(data.urlToImage)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(ivArticleImage)

            root.setOnClickListener {
                listener.onClick(data)
            }
        }
    }

    override fun getItemCount() = listNews.size

    fun setData(data: List<NewsResponse.ArticlesItem>) {
        listNews.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData() {
        listNews.clear()
        notifyDataSetChanged()
    }
}