package kz.ioka.android.ioka.util

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart


internal fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendBlocking(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

internal fun Context.showErrorToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

internal fun View.setStrokeColor(
    @ColorRes defaultColor: Int,
    @ColorRes focusedColor: Int,
) {
    val states = arrayOf(
        intArrayOf(-R.attr.state_focused),
        intArrayOf(R.attr.state_focused),
    )

    val colors = intArrayOf(
        ContextCompat.getColor(context, defaultColor), ContextCompat.getColor(context, focusedColor)
    )

    val stateList = ColorStateList(states, colors)

    (background as? GradientDrawable)?.setStroke(1.toPx.toInt(), stateList)
}