package id.my.okisulton.newsapplicationmandiri.utils

import android.content.Context
import android.view.View
import java.io.IOException

/**
 * Created by Oki Sulton on 03/08/2022.
 */
object ExtensionFunctions {
    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.GONE
    }

    fun getDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}