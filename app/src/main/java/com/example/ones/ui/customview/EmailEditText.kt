package com.example.ones.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr), TextWatcher {

    private var parentLayout: TextInputLayout? = null

    private fun findParentTextInputLayout(): TextInputLayout? {
        var parentView = parent
        while (parentView != null && parentView !is TextInputLayout) {
            parentView = parentView.parent
        }
        return parentView as? TextInputLayout
    }

    init {
        post {
            parentLayout = findParentTextInputLayout()
            Log.d("EmailEditText", "Parent resolved: $parentLayout")
        }
        addTextChangedListener(this)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val email = s?.toString() ?: ""
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            parentLayout?.isErrorEnabled = true
            parentLayout?.error = "Email tidak valid"
            Log.d("EmailEditText", "Setting error: Email tidak valid")
        } else {
            parentLayout?.isErrorEnabled = false
            parentLayout?.error = null
            Log.d("EmailEditText", "Clearing error")
        }
    }

    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}