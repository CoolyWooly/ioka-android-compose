package kz.ioka.android.ioka.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
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
