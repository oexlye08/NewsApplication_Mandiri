package id.my.okisulton.newsapplicationmandiri.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.my.okisulton.newsapplicationmandiri.databinding.ItemCategoryBinding
import id.my.okisulton.newsapplicationmandiri.network.model.Category

/**
 * Created by Oki Sulton on 03/08/2022.
 */
class CategoryAdapter(
    private val listCategory : ArrayList<Category.DataItem>,
    private val listener: OnAdapterListener
): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var rowIndex: Int? = -1
    interface OnAdapterListener {
        fun onClick(result: Category.DataItem)
    }

    class ViewHolder(val binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = listCategory[position]

        holder.binding.apply {
            tvCategory.text = data.category
            root.setOnClickListener {
                listener.onClick(data)
                rowIndex = position
                notifyDataSetChanged()
//                cardCategory.setCardBackgroundColor(Color.parseColor("#ff0099cc"))
            }

            Log.d("TAG", "onBindViewHolder: $rowIndex + $position")

            if (rowIndex == position) {
                cardCategory.setCardBackgroundColor(Color.parseColor("#ff0099cc"))
            } else {
                cardCategory.setCardBackgroundColor(Color.parseColor("#ff669900"))
            }

        }
    }

    override fun getItemCount() = listCategory.size

    fun setData(data: List<Category.DataItem>) {
        listCategory.addAll(data)
        notifyDataSetChanged()
    }

    fun clearData() {
        listCategory.clear()
        notifyDataSetChanged()
    }
}