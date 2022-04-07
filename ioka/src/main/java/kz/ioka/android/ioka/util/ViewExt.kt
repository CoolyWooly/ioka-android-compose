package kz.ioka.android.ioka.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.appcompat.widget.AppCompatImageButton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kz.ioka.android.ioka.R

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
    val layout = LayoutInflater.from(this).inflate(R.layout.ioka_error_view, null)
    val tvErrorText = layout.findViewById<TextView>(R.id.tvErrorText)
    val btnClose = layout.findViewById<AppCompatImageButton>(R.id.btnClose)

    tvErrorText.text = message

    val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.BOTTOM, 0, 0)
    toast.view = layout
    toast.view!!.setOnTouchListener { _, event -> //Make sure the view is accessible
        toast.view!!.performClick()

        if (btnClose != null) {
            //Set the touch location to the absolute screen location
            event.setLocation(event.rawX, event.rawY)
            //Send the touch event to the view
            btnClose.onTouchEvent(event)
        }
        false
    }
    toast.show()

    btnClose.setOnClickListener {
        toast.cancel()
    }
}

internal fun ActivityResult.getStringExtra(name: String, default: String): String {
    return data?.getStringExtra(name) ?: default
}