package com.seftian.storyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.doAfterTextChanged
import com.seftian.storyapp.data.mappers.toUserEntity
import com.seftian.storyapp.data.model.ApiResponse
import com.seftian.storyapp.data.model.LoginModel
import com.seftian.storyapp.databinding.ActivityMainBinding
import com.seftian.storyapp.ui.activities.home.HomeActivity
import com.seftian.storyapp.ui.activities.login.LoginViewModel
import com.seftian.storyapp.ui.activities.signup.SignUpActivity
import com.seftian.storyapp.util.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentSignup = Intent(this, SignUpActivity:: class.java )
        val intentHome = Intent(this, HomeActivity::class.java)
        val customDialog = Helper.customDialog(this@MainActivity)

        viewModel.apiResponse.observe(this){apiResponse->
            when(apiResponse){
                ApiResponse.Loading -> customDialog.show()

                is ApiResponse.Success -> {
                    val data = apiResponse.data

                    customDialog.dismiss()
                    Toast.makeText(this,data.message, Toast.LENGTH_SHORT).show()
                    viewModel.updateUserLoginData(data.toUserEntity())
                    viewModel.setTokenToPref(data.loginResult.token)
                    startActivity(intentHome)
                }

                is ApiResponse.Error -> {
                    val message = apiResponse.message

                    customDialog.dismiss()
                    Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isLoggedIn.observe(this) {
            if(it){
                startActivity(intentHome)
            }
        }

        binding.apply {
            btnLogin.isEnabled = false

            edLoginPassword.doAfterTextChanged {
                val isValidEmail = edLoginEmail.text.toString().isNotEmpty() && layoutLoginEmail.error == null
                val isValidPassword = it.toString().isNotEmpty() && layoutLoginPassword.error == null

                btnLogin.isEnabled = isValidEmail && isValidPassword
            }
        }

        binding.apply {

            btnLogin.setOnClickListener {
                val payload = LoginModel(
                    email = edLoginEmail.text.toString(),
                    password = edLoginPassword.text.toString()
                )

                viewModel.postLogin(payload)
            }

            btnSignup.setOnClickListener {
                startActivity(intentSignup)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        binding.edLoginEmail.text?.clear()
        binding.edLoginPassword.text?.clear()
    }
}