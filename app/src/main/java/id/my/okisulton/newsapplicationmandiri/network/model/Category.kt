package id.my.okisulton.newsapplicationmandiri.network.model

import com.google.gson.annotations.SerializedName

data class Category(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("name")
	val name: String? = null
){
	data class DataItem(

		@field:SerializedName("category")
		val category: String? = null
	)
}
