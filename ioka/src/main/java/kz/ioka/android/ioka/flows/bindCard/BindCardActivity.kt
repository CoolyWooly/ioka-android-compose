package kz.ioka.android.ioka.flows.bindCard

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isInvisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.flows.bindCard.BindCardRequestState.*
import kz.ioka.android.ioka.flows.bindCard.Configuration.Companion.DEFAULT_FONT
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.uikit.Callback
import kz.ioka.android.ioka.uikit.ErrorView
import kz.ioka.android.ioka.uikit.StateButton
import kz.ioka.android.ioka.util.textChanges
import kz.ioka.android.ioka.util.toPx
import kz.ioka.android.ioka.viewBase.BaseActivity

@AndroidEntryPoint
internal class BindCardActivity : BaseActivity(), View.OnClickListener {

    private val infoViewModel: CardInfoViewModel by viewModels()
    private val saveCardViewModel: SaveCardViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar
    private lateinit var cardNumberContainer: LinearLayoutCompat
    private lateinit var etCardNumber: AppCompatEditText
    private lateinit var etExpireDate: AppCompatEditText
    private lateinit var etCvv: AppCompatEditText
    private lateinit var ivEmitter: AppCompatImageView
    private lateinit var ivBrand: AppCompatImageView
    private lateinit var btnScan: AppCompatImageButton
    private lateinit var vError: ErrorView
    private lateinit var btnSave: StateButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_card)

        bindViews()
        setupListeners()
        setupViews()
        setConfiguration()
        observeData()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        cardNumberContainer = findViewById(R.id.cardNumberContainer)
        etCardNumber = findViewById(R.id.etCardNumber)
        etExpireDate = findViewById(R.id.etExpireDate)
        etCvv = findViewById(R.id.etCvv)
        ivEmitter = findViewById(R.id.ivEmitter)
        ivBrand = findViewById(R.id.ivBrand)
        btnScan = findViewById(R.id.btnScan)
        vError = findViewById(R.id.vError)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun setupViews() {
        vError.registerLifecycleOwner(lifecycle)
        btnSave.setCallback(object : Callback {
            override fun onSuccess(): () -> Unit = {
                lifecycleScope.launch {
                    delay(500)
                    finish()
                }
            }
        })
    }

    private fun setConfiguration() {
        launcher<BindCardLauncher>()?.configuration?.apply {
            vToolbar.setTitle(toolbarTitleRes)

            (cardNumberContainer.background as GradientDrawable).cornerRadius =
                fieldCornerRadius.toPx
            (etExpireDate.background as GradientDrawable).cornerRadius = fieldCornerRadius.toPx
            (etCvv.background as GradientDrawable).cornerRadius = fieldCornerRadius.toPx

            btnSave.setConfiguration(
                saveButtonCornerRadius,
                saveButtonBackgroundColorRes,
                saveButtonTextRes
            )

            if (fontRes != DEFAULT_FONT) {
                val typeface = ResourcesCompat.getFont(this@BindCardActivity, fontRes)
                checkNotNull(typeface)

                etCardNumber.typeface = typeface
                etExpireDate.typeface = typeface
                etCvv.typeface = typeface
                btnSave.setTypeface(typeface)
            }
        }
    }

    private fun setupListeners() {
        etCardNumber.textChanges().debounce(200).onEach {
            infoViewModel.onCardPanEntered(it.toString().replace(" ", ""))
        }
            .launchIn(lifecycleScope)

        etCardNumber.doOnTextChanged { text, _, _, _ ->
            saveCardViewModel.onCardPanEntered(text.toString().replace(" ", ""))
        }

        etExpireDate.doOnTextChanged { text, _, _, _ ->
            saveCardViewModel.onExpireDateEntered(text.toString().replace("/", ""))
        }

        etCvv.doOnTextChanged { text, _, _, _ ->
            saveCardViewModel.onCvvEntered(text.toString())
        }

        etCardNumber.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            val strokeWidth = if (hasFocus) 1.toPx.toInt() else 0

            val back = cardNumberContainer.background as GradientDrawable

            back.mutate()
            back.setStroke(
                strokeWidth,
                ContextCompat.getColor(this, R.color.ioka_color_primary)
            )

            cardNumberContainer.background = back
        }

        vToolbar.setNavigationOnClickListener(this)
        btnScan.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    private fun observeData() {
        with(infoViewModel) {
            cardBrand.observe(this@BindCardActivity) {
                if (it.isPresent()) {
                    ivBrand.setImageDrawable(getDrawableFromRes(it.get()))
                }

                ivBrand.isInvisible = it.isNotPresent()
            }

            cardEmitter.observe(this@BindCardActivity) {
                if (it.isPresent()) {
                    ivEmitter.setImageDrawable(getDrawableFromRes(it.get()))
                }

                ivEmitter.isInvisible = it.isNotPresent()
            }
        }

        with(saveCardViewModel) {
            bindRequestState.observe(this@BindCardActivity) {
                handleState(it)
            }
        }
    }

    private fun handleState(state: BindCardRequestState) {
        val buttonState = when (state) {
            SUCCESS -> ButtonState.Success

            LOADING -> ButtonState.Loading

            DISABLED -> ButtonState.Disabled

            else -> ButtonState.Default
        }

        btnSave.setState(buttonState)

        if (state is ERROR) {
            vError.show(state.cause ?: getString(R.string.common_server_error))
        } else {
            vError.hide()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                saveCardViewModel.onSaveClicked(
                    etCardNumber.text.toString().replace(" ", ""),
                    etExpireDate.text.toString(),
                    etCvv.text.toString()
                )
            }
            btnScan -> {
                // todo implement scanner
            }
            else -> {
                onBackPressed()
            }
        }
    }

}