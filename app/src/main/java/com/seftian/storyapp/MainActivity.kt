package com.seftian.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.seftian.storyapp.data.model.LoginModel
import com.seftian.storyapp.ui.activities.signup.SignUpActivity
import com.seftian.storyapp.databinding.ActivityMainBinding
import com.seftian.storyapp.ui.activities.login.LoginViewModel
import com.seftian.storyapp.util.Helper
import com.seftian.storyapp.util.MainViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentSignup = Intent(this, SignUpActivity:: class.java )
        val customDialog = Helper.customDialog(this@MainActivity)




        viewModel.loading.observe(this) {
            if (it) {
                customDialog.show()
            } else {
                customDialog.dismiss()
            }
        }

        viewModel.responseLogin.observe(this){
            if(it != null && !it.error){
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorResponse.observe(this){
            if(it != null){
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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