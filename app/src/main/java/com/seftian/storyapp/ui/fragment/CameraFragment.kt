package com.seftian.storyapp.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.seftian.storyapp.R
import com.seftian.storyapp.databinding.FragmentCameraBinding
import com.seftian.storyapp.ui.activities.addstory.AddStoryViewModel
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CameraFragment(private val viewModel: AddStoryViewModel) : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var btnCamera : ImageView
    private lateinit var previewView: PreviewView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(layoutInflater)
        btnCamera = binding.btnCamera
        previewView = binding.viewFinder
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageCapture = ImageCapture.Builder().build()
        openCamera()

        btnCamera.setOnClickListener {
            captureImage()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCamera() {
        val cameraInstance = ProcessCameraProvider.getInstance(requireContext())

        cameraInstance.addListener({
            val cameraProvider = cameraInstance.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), resources.getString(R.string.fail_open_camera), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            getOutputDirectory(),
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Helper.rotateFile(photoFile, isBackCamera = true)
                    if (savedUri != null) {
                        hideCameraFragment()
                        viewModel.setUriPhoto(savedUri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), resources.getString(R.string.fail_capture_image) ,Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun hideCameraFragment() {
        parentFragmentManager.beginTransaction().remove(this).commit()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, requireContext().getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: requireContext().filesDir
    }

}
