package id.my.okisulton.newsapplicationmandiri.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import id.my.okisulton.newsapplicationmandiri.R
import id.my.okisulton.newsapplicationmandiri.databinding.ActivityDetailBinding
import id.my.okisulton.newsapplicationmandiri.network.model.NewsResponse
import id.my.okisulton.newsapplicationmandiri.utils.Constants.INTENT_WITH_DATA

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding!!
    var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingProgressDialog()
    }

    override fun onStart() {
        progressDialog?.show()
        getUrl()
        super.onStart()
    }

    private fun getUrl() {
        val data =
            intent.getParcelableExtra<NewsResponse.ArticlesItem>(INTENT_WITH_DATA) as NewsResponse.ArticlesItem
        initWebView(data.url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String?) {
        progressDialog?.show()

        binding.webView.apply {
            webViewClient = object :WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressDialog?.dismiss()
                }
            }
            loadUrl(url.toString())

            val webSetting = settings
            //activate JS
            webSetting.javaScriptEnabled = true
            webSetting.domStorageEnabled = true
        }
    }

    private fun settingProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.loading_animation)
        progressDialog.let {
            it?.setCancelable(false)
            it?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}