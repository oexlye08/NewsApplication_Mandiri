package id.my.okisulton.newsapplicationmandiri.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import id.my.okisulton.newsapplicationmandiri.R
import id.my.okisulton.newsapplicationmandiri.adapter.CategoryAdapter
import id.my.okisulton.newsapplicationmandiri.adapter.NewsAdapter
import id.my.okisulton.newsapplicationmandiri.databinding.ActivityMainBinding
import id.my.okisulton.newsapplicationmandiri.network.api.ApiServices
import id.my.okisulton.newsapplicationmandiri.network.model.Category
import id.my.okisulton.newsapplicationmandiri.network.model.NewsResponse
import id.my.okisulton.newsapplicationmandiri.utils.Constants.API_KEY
import id.my.okisulton.newsapplicationmandiri.utils.Constants.CountryCode
import id.my.okisulton.newsapplicationmandiri.utils.Constants.INTENT_WITH_DATA
import id.my.okisulton.newsapplicationmandiri.utils.Constants.QUERY_PER_PAGE
import id.my.okisulton.newsapplicationmandiri.utils.ExtensionFunctions.getDataFromAsset
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var newsAdapter: NewsAdapter
    private val TAG: String = "MainActivity"
    private var page = 1
    private var totalPage = 0
    private var category = "general"
    private var qSearch = ""
    var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Headline News"

        settingProgressDialog()
    }

    override fun onStart() {
        super.onStart()
        progressDialog?.show()
        setupRvCategory()
        getCategory()

        setupRvNews()
        getData(category, qSearch)

        onSwap()
    }

    override fun onResume() {
        super.onResume()
        progressDialog?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.isSubmitButtonEnabled = true
        //Search button clicked
        searchView?.setOnSearchClickListener {
            searchView.maxWidth = android.R.attr.width
        }
        //Close button clicked
        searchView?.setOnCloseListener {
            clearSearch()
            //Collapse the action view
            searchView.onActionViewCollapsed()
            searchView.maxWidth = 0
            true
        }

        searchView?.setOnQueryTextListener(this)
        return true
    }

    private fun clearSearch() {
        qSearch = ""
        progressDialog?.show()
        getData(category, qSearch)
    }

    private fun setupRvCategory() {
        categoryAdapter = CategoryAdapter(
            arrayListOf(),
            object : CategoryAdapter.OnAdapterListener {
                override fun onClick(result: Category.DataItem) {
                    val categoryName = result.category
                    category = categoryName.toString()
                    newsAdapter.clearData()
                    progressDialog?.show()
                    getData(category, qSearch)
                }
            }
        )

        binding.includeMain.rvListCategory.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = categoryAdapter
        }
    }

    private fun getCategory() {
        val jsonFile = getDataFromAsset(this, "category.json")
        val categoryList = Gson().fromJson(jsonFile, Category::class.java)
        showResult(categoryList)
    }

    private fun setupRvNews() {
        newsAdapter = NewsAdapter(
            arrayListOf(),
            object : NewsAdapter.OnAdapterListener {
                override fun onClick(result: NewsResponse.ArticlesItem) {
                    val moveData = Intent(this@MainActivity, DetailActivity::class.java)
                    moveData.putExtra(INTENT_WITH_DATA, result)
                    startActivity(moveData)
                }
            }
        )

        binding.includeMain.rvListNews.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.itemCount
                    val pastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    val isLastPosition = visibleItemCount.minus(1) == pastVisibleItem
                    val total = newsAdapter.itemCount

                    if (isLastPosition && page < totalPage) {
                        if (visibleItemCount + pastVisibleItem >= total) {
                            binding.includeMain.linearLoad.show()
                            page = page.plus(1)
                            getData(category, qSearch)
                        }
                    }
                }
            })
            adapter = newsAdapter
        }
    }

    private fun getData(categoryName: String?, query: String?) {
        val swipeRefresh = binding.includeMain.swipeRefresh
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }

        val parameters = HashMap<String, String>()

        parameters["country"] = CountryCode
        parameters["page"] = page.toString()
        parameters["pageSize"] = QUERY_PER_PAGE.toString()
        parameters["apiKey"] = API_KEY
        parameters["category"] = categoryName.toString()
        parameters["q"] = query.toString()

        Log.d(TAG, "getData: $categoryName")

        ApiServices.endpoint.getNews(
            parameters = parameters
        ).enqueue(object : Callback<NewsResponse>{
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    progressDialog?.hide()
                    binding.includeMain.linearLoad.hide()
                    val data = response.body()
                    if (data != null) {
                        totalPage = data.totalResults!!
                    }
                    showList(data!!)
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                progressDialog?.hide()
                binding.includeMain.linearLoad.hide()
                binding.fab.show()
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showResult(categoryList: Category) {
        val listCategory = categoryList.data
        categoryAdapter.setData(listCategory)
    }

    private fun showList(newsList: NewsResponse) {
        val listNews = newsList.articles
        newsAdapter.setData(listNews)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            qSearch = query
            progressDialog?.show()
            getData(category, qSearch)
            page = 1
            newsAdapter.clearData()
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }

    private fun settingProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.loading_animation)
        progressDialog.let {
            it?.setCancelable(false)
            it?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun onSwap() {
        val swipeRefresh = binding.includeMain.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            newsAdapter.clearData()
            progressDialog?.show()
            page = 1
            getData(category, qSearch)
        }
    }
}