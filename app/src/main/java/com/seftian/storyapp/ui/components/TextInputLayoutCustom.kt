package com.seftian.storyapp.ui.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.seftian.storyapp.R

class TextInputLayoutCustom @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.textInputStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    enum class ValidationType {
        EMAIL,
        PASSWORD,
        NOTHING
    }

    private var validationType = ValidationType.NOTHING
    private var textWatcher: TextWatcher? = null
    private var minLength: Int = 6

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextInputLayoutCustom)
        val typeOrdinal = typedArray.getInt(R.styleable.TextInputLayoutCustom_validationType, ValidationType.EMAIL.ordinal)
        minLength = typedArray.getInt(R.styleable.TextInputLayoutCustom_minLength, 6)
        validationType = ValidationType.values()[typeOrdinal]
        typedArray.recycle()

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text != null && !isValidInput(text.toString()) && text.isNotEmpty()) {
                    showError()
                } else {
                    hideError()
                }
            }

            override fun afterTextChanged(text: Editable?) {

            }
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        editText?.addTextChangedListener(textWatcher)
    }

    private fun isValidInput(input: String): Boolean {
        return when (validationType) {
            ValidationType.EMAIL -> isValidEmail(input)
            ValidationType.PASSWORD -> isValidPassword(input)
            ValidationType.NOTHING -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        val alphanumericRegex = Regex("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")
        return password.length >= minLength && password.matches(alphanumericRegex)
    }

    private fun showError() {
        error = when(validationType) {
            ValidationType.EMAIL -> resources.getString(R.string.error_email)
            ValidationType.PASSWORD -> {
                errorIconDrawable = null
                resources.getString(R.string.error_password, minLength.toString())
            }

            else -> ""
        }
    }

    private fun hideError() {
        error = null
    }
}