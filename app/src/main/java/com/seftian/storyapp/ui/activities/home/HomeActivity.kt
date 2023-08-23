package com.seftian.storyapp.ui.activities.home


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.seftian.storyapp.R
import com.seftian.storyapp.databinding.ActivityHomeBinding
import com.seftian.storyapp.ui.fragment.HomeFragment
import com.seftian.storyapp.ui.fragment.maps.MapsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var toolbar: MaterialToolbar
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.topAppBar
        setSupportActionBar(toolbar)
        shapeToolbar()

        val initialFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.wakwaw, initialFragment)
            .commit()

        binding.navBottom.setOnItemSelectedListener {item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_all_story -> HomeFragment()
                R.id.navigation_map_story -> MapsFragment()
                else -> HomeFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.wakwaw, fragment)
                .commit()

            return@setOnItemSelectedListener true
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
        viewModel.deleteRemoteKeys()
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

    companion object{
        const val CODE_TO_REFRESH = 100
    }
}