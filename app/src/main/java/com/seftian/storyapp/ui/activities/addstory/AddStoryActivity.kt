package com.seftian.storyapp.ui.activities.addstory


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.seftian.storyapp.R
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.databinding.ActivityAddStoryBinding
import com.seftian.storyapp.ui.activities.home.HomeActivity
import com.seftian.storyapp.ui.fragment.CameraFragment
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()
    private lateinit var cameraFragment: CameraFragment
    private var getFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customDialog = Helper.customDialog(this)
        Helper.shapeToolbar(binding.topAppBar, resources.getDimension(R.dimen.corner_size))

        cameraFragment = CameraFragment(viewModel)

        binding.ivAddImage.setOnClickListener {
            val options = arrayOf(
                resources.getString(R.string.camera),
                resources.getString(R.string.gallery)
            )
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.choose_image_from))
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            if (allPermissionsGranted()) {
                                showCameraFragment()
                            } else {
                                requestPermission()
                            }
                        }
                        1 -> {
                            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
                        }
                    }
                }
                .show()

        }

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSubmitImage.setOnClickListener {
            if (getFile != null) {

                val description = binding.edDescription.text.toString()
                viewModel.compressAndUploadStory(getFile!!, description)
            } else {
                Toast.makeText(this, resources.getString(R.string.add_picture_first), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.apiResponse.observe(this) { apiResponse ->
            when (apiResponse) {
                is ApiResponse.Loading -> {
                    customDialog.show()
                }
                is ApiResponse.Success -> {
                    customDialog.dismiss()
                    setResult(HomeActivity.CODE_TO_REFRESH)
                    finish()
                    Toast.makeText(this, apiResponse.data.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponse.Error -> {
                    customDialog.dismiss()
                    Toast.makeText(this, apiResponse.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uriPhoto.observe(this){uri ->
            if(uri != null){
                getFileFromUri(uri).let { file ->
                    getFile = file
                }
                binding.ivAddImage.setImageURI(uri)
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (cameraFragment.isAdded) {
                hideCameraFragment()
            } else {
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                viewModel.setUriPhoto(selectedImageUri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE_PERMISSION){
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.dont_have_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }
    }

    private fun showCameraFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, cameraFragment)
        fragmentTransaction.commit()
    }


    private fun hideCameraFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(cameraFragment)
        fragmentTransaction.commit()
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = this.contentResolver.openInputStream(uri)
        val outputFile = File(this.cacheDir, "temp_file")

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }

        return outputFile
    }



    companion object{

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
        private const val REQUEST_CODE_PICK_IMAGE = 11
    }
}