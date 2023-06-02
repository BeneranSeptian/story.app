package com.seftian.storyapp.ui.activities.home


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.seftian.storyapp.R
import com.seftian.storyapp.data.mappers.toStoriesEntity
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.databinding.ActivityHomeBinding
import com.seftian.storyapp.domain.Story
import com.seftian.storyapp.ui.activities.addstory.AddStoryActivity
import com.seftian.storyapp.ui.activities.detail.DetailStoryActivity
import com.seftian.storyapp.ui.activities.home.adapter.StoryAdapter
import com.seftian.storyapp.ui.activities.home.adapter.StoryAdapterClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity :
    AppCompatActivity(),
    SwipeRefreshLayout.OnRefreshListener,
    StoryAdapterClickListener{

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var toolbar: MaterialToolbar

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var storyList: List<Story> = emptyList()
    private var isEndReached = false
    private var page = 1

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.topAppBar
        setSupportActionBar(toolbar)
        shapeToolbar()

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)

        recyclerView = binding.rvStory
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StoryAdapter(storyList, this@HomeActivity, this)
        recyclerView.adapter = adapter


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == CODE_TO_REFRESH) {
                viewModel.getAllStoriesFromRemote(1)
                viewModel.deleteAllStoriesFromLocal()
            }
        }
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            resultLauncher.launch(intent)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    val isNewEndReached = (visibleItemCount + firstVisibleItemPosition) >= totalItemCount

                    if (isNewEndReached && !isEndReached && layoutManager.findLastCompletelyVisibleItemPosition() == totalItemCount - 1) {
                        isEndReached = true
                        viewModel.getAllStoriesFromRemote(page)
                        page++
                    } else {
                        isEndReached = false
                    }
                }
            }
        })

        viewModel.apiResponse.observe(this){apiResponse->
            when(apiResponse){
                is ApiResponse.Loading -> {
                    swipeRefreshLayout.isRefreshing = true
                }

                is ApiResponse.Success -> {
                    val data = apiResponse.data
                    swipeRefreshLayout.isRefreshing = false

                    val listStoryEntity = data.listStory.map{storyResponse ->
                        storyResponse.toStoriesEntity()
                    }.toList()

                    viewModel.upsertStoriesToLocal(listStoryEntity)

                }

                is ApiResponse.Error -> {
                    val errorMessage = apiResponse.message
                    swipeRefreshLayout.isRefreshing = false

                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.userStories.observe(this){
            adapter.updateStoryList(it)
        }

        onBackPressedDispatcher.addCallback(this ) {
            finishAffinity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_icon -> {
                    showPopupMenu()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    override fun onRefresh() {
        viewModel.getAllStoriesFromRemote(1)
        viewModel.deleteAllStoriesFromLocal()
        recyclerView.scrollToPosition(0)
    }

    private fun shapeToolbar(){

        val radius = resources.getDimension(R.dimen.corner_size)
        val toolbar = binding.topAppBar


        val materialShapeDrawable = toolbar.background as MaterialShapeDrawable
        materialShapeDrawable.shapeAppearanceModel = materialShapeDrawable.shapeAppearanceModel
            .toBuilder()
            .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()
    }

    private fun logout(){
        viewModel.logout()
        viewModel.deleteAllStoriesFromLocal()
        viewModel.deleteToken()
        finish()
    }

    private fun showPopupMenu() {
        val anchorView = findViewById<View>(R.id.action_icon)

        val popupMenu = PopupMenu(this@HomeActivity, anchorView)
        popupMenu.inflate(R.menu.setting_options_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    logout()
                    true
                }
                R.id.menu_change_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onItemClick(storyId: String) {
        val intentToDetail = Intent(this, DetailStoryActivity::class.java)
        intentToDetail.putExtra("storyId", storyId)
        startActivity(intentToDetail)
    }

    companion object{
        const val CODE_TO_REFRESH = 100
    }
}