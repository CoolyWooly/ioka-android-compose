package kz.ioka.android.ioka.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.toPx


class ErrorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr), LifecycleObserver {

    private lateinit var tvErrorText: AppCompatTextView
    private lateinit var btnClose: AppCompatImageButton

    private lateinit var scope: LifecycleCoroutineScope

    private var autoInvisibilityJob: Job? = null

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_error, this, true)

        bindViews(root)
        background = AppCompatResources.getDrawable(context, R.drawable.bg_error)
        orientation = HORIZONTAL
        isVisible = false

        btnClose.setOnClickListener { hide() }
    }

    private fun bindViews(root: View) {
        tvErrorText = root.findViewById(R.id.tvErrorText)
        btnClose = root.findViewById(R.id.btnClose)
    }

    fun registerLifecycleOwner(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }

    fun show(text: String? = null) {
        text?.let { tvErrorText.text = text }

        isVisible = true

        autoInvisibilityJob = scope.launch {
            delay(2000)

            isVisible = false
        }
    }

    fun hide() {
        isVisible = false

        autoInvisibilityJob?.cancel()
        autoInvisibilityJob = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        autoInvisibilityJob?.cancel()
        autoInvisibilityJob = null
    }

}