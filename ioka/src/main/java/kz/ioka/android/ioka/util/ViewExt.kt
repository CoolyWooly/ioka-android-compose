package kz.ioka.android.ioka.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
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

internal fun View.showErrorSnackbar(message: String) {
    val snackbar = Snackbar.make(this, "", Snackbar.LENGTH_LONG)
    val layout = snackbar.view as SnackbarLayout
    val textView =
        layout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    textView.visibility = View.INVISIBLE

    val snackView: View = LayoutInflater.from(context).inflate(R.layout.view_error, null)
    val tvErrorText = snackView.findViewById<TextView>(R.id.tvErrorText)
    val btnClose = snackView.findViewById<AppCompatImageButton>(R.id.btnClose)
    tvErrorText.text = message
    btnClose.setOnClickListener { snackbar.dismiss() }

    layout.setPadding(0, 0, 0, 0)
    layout.addView(snackView, 0)
    layout.setBackgroundColor(
        ContextCompat.getColor(
            context, R.color.ioka_color_static_transparent
        )
    )
    snackbar.show()
}

internal fun Context.showErrorToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//    val layout = LayoutInflater.from(this).inflate(R.layout.view_error, null)
//    val tvErrorText = layout.findViewById<TextView>(R.id.tvErrorText)
//    val btnClose = layout.findViewById<AppCompatImageButton>(R.id.btnClose)
//    tvErrorText.text = message
//
//    val toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
//    toast.setGravity(Gravity.BOTTOM, 0, 0)
//    toast.view = layout
//    toast.show()
//
//    btnClose.setOnClickListener { toast.cancel() }
}
