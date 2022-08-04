package id.my.okisulton.newsapplicationmandiri.activity

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import id.my.okisulton.newsapplicationmandiri.databinding.ActivityDetailBinding
import id.my.okisulton.newsapplicationmandiri.network.model.NewsResponse
import id.my.okisulton.newsapplicationmandiri.utils.Constants.INTENT_WITH_DATA

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        getUrl()
        super.onStart()
    }

    private fun getUrl() {
        val data =
            intent.getParcelableExtra<NewsResponse.ArticlesItem>(INTENT_WITH_DATA) as NewsResponse.ArticlesItem
        initWebView(data.url)
    }

    private fun initWebView(url: String?) {
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(url.toString())

            val webSetting = settings
            //activate JS
            webSetting.javaScriptEnabled = true
            webSetting.domStorageEnabled = true
        }

    }
}