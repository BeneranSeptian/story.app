package com.seftian.storyapp.ui.activities.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.seftian.storyapp.util.MainViewModelFactory
import com.seftian.storyapp.R
import com.seftian.storyapp.data.model.SignUpModel
import com.seftian.storyapp.databinding.ActivitySignUpBinding
import com.seftian.storyapp.util.Helper


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignupViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customDialog = Helper.customDialog(this@SignUpActivity)


        val factory = MainViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[SignupViewModel::class.java]

        viewModel.loading.observe(this) {
            if (it) {
                customDialog.show()
            } else {
                customDialog.show()
            }
        }

        viewModel.responseSignUp.observe(this){
            if(it != null && !it.error){
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorResponse.observe(this){
            if(it != null){
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }


        binding.apply {
            btnSignup.isEnabled = false

            edRegisterPassword2.doAfterTextChanged { editable ->
                val password1 = edRegisterPassword.text.toString()
                val password2 = editable.toString()
                val name = edRegisterName.text.toString()

                val isNameValid = name.isNotEmpty()
                val isEmailValid = edRegisterEmail.text.toString().isNotEmpty() && layoutRegisterEmail.error == null
                val isPassword1Valid = password1.isNotEmpty() && layoutRegisterPassword.error == null
                val isPassword2Valid = password2.isNotEmpty() && layoutRegisterPassword2.error == null && (password1 == password2)

                btnSignup.isEnabled = isNameValid && isEmailValid && isPassword1Valid && isPassword2Valid

                if (password2.isEmpty()) {
                    layoutRegisterPassword2.error = null
                    return@doAfterTextChanged
                }

                if (password1 != password2) {
                    layoutRegisterPassword2.error = getString(R.string.passwords_dont_match)
                    layoutRegisterPassword2.errorIconDrawable = null
                    return@doAfterTextChanged
                }

                layoutRegisterPassword2.error = null
            }


            btnSignup.setOnClickListener{

                val payload = SignUpModel(
                    name = edRegisterName.text.toString(),
                    email = edRegisterEmail.text.toString(),
                    password = edRegisterPassword.text.toString(),
                )
                viewModel.postSignUp(payload)

            }
        }
    }

}