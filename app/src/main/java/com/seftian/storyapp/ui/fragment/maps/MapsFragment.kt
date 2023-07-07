package com.seftian.storyapp.ui.fragment.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.databinding.FragmentMapsBinding
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap:GoogleMap
    private lateinit var mapView:MapView

    private val viewModel: MapsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater)
        mapView = binding.mapView

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        (activity as AppCompatActivity).supportActionBar?.hide()
        return binding.root
    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap
        var page = 1

        val customDialog = Helper.customDialog(requireContext())

        mMap.uiSettings.isZoomControlsEnabled = true

        binding.btnFetchMore.setOnClickListener {
            page += 1
            viewModel.getStoryWithLocation(page)
        }

        viewModel.storyWithLocation.observe(viewLifecycleOwner){apiResponse ->

            when(apiResponse){
                is ApiResponse.Error -> {
                    customDialog.hide()
                    Toast.makeText(requireContext(), apiResponse.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponse.Loading -> {
                    customDialog.show()
                }
                is ApiResponse.Success -> {
                    customDialog.dismiss()
                    val stories = apiResponse.data.listStory
                    stories.forEach { story->
                        if(story.lat != null && story.lon != null){
                            val latLong = LatLng(story.lat.toDouble(), story.lon.toDouble())
                            mMap.addMarker(MarkerOptions().position(latLong).title(story.description))
                        }
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

}