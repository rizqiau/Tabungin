package com.example.ones.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText @JvmOverloads constructor(
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
        }
        addTextChangedListener(this)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val password = s?.toString() ?: ""
        if (password.isNotEmpty() && password.length < 8) {
            parentLayout?.isErrorEnabled = true
            parentLayout?.error = "Password harus minimal 8 karakter"
        } else {
            parentLayout?.isErrorEnabled = false
            parentLayout?.error = null
        }
    }

    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}