package com.seftian.storyapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.seftian.storyapp.databinding.FragmentHomeBinding
import com.seftian.storyapp.ui.activities.addstory.AddStoryActivity
import com.seftian.storyapp.ui.activities.detail.DetailStoryActivity
import com.seftian.storyapp.ui.activities.home.HomeActivity.Companion.CODE_TO_REFRESH
import com.seftian.storyapp.ui.activities.home.HomeViewModel
import com.seftian.storyapp.ui.activities.home.adapter.LoadingStateAdapter
import com.seftian.storyapp.ui.activities.home.adapter.StoryAdapter
import com.seftian.storyapp.ui.activities.home.adapter.StoryAdapterClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    StoryAdapterClickListener{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

               swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)

        recyclerView = binding.rvStory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = StoryAdapter( requireContext(), this)
        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == CODE_TO_REFRESH) {
                adapter.refresh()
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(activity, AddStoryActivity::class.java)
            resultLauncher.launch(intent)
        }

        viewModel.userStories.observe(viewLifecycleOwner){
            adapter.submitData(lifecycle, it)
            swipeRefreshLayout.isRefreshing = false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        adapter.refresh()
    }

    override fun onItemClick(storyId: String) {
        val intentToDetail = Intent(activity, DetailStoryActivity::class.java)
        intentToDetail.putExtra("storyId", storyId)
        startActivity(intentToDetail)
    }
}