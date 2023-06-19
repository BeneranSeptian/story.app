package com.seftian.storyapp.ui.activities.home


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
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
import com.seftian.storyapp.databinding.ActivityHomeBinding
import com.seftian.storyapp.ui.activities.addstory.AddStoryActivity
import com.seftian.storyapp.ui.activities.detail.DetailStoryActivity
import com.seftian.storyapp.ui.activities.home.adapter.LoadingStateAdapter
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

        adapter = StoryAdapter( this@HomeActivity, this)
        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )

        viewModel._testingStory.observe(this){
            adapter.submitData(lifecycle, it)
        }


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == CODE_TO_REFRESH) {
//                viewModel.getAllStoriesFromRemote(1)
                viewModel.deleteAllStoriesFromLocal()
            }
        }
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            resultLauncher.launch(intent)
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