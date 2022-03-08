package kz.ioka.android.ioka.presentation.flows.bindCard

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardRequestState.*
import kz.ioka.android.ioka.presentation.flows.bindCard.Configuration.Companion.DEFAULT_FONT
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.presentation.webView.WebViewLauncher
import kz.ioka.android.ioka.uikit.*
import kz.ioka.android.ioka.util.toPx
import kz.ioka.android.ioka.viewBase.BaseActivity

@AndroidEntryPoint
internal class BindCardActivity : BaseActivity(), View.OnClickListener {

    private val infoViewModel: CardInfoViewModel by viewModels()
    private val saveCardViewModel: SaveCardViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpireDate: AppCompatEditText
    private lateinit var etCvv: AppCompatEditText
    private lateinit var vError: ErrorView
    private lateinit var btnSave: StateButton

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                btnSave.setState(ButtonState.Success)
            } else {
                btnSave.setState(ButtonState.Default)
                vError.show()
            }
        }

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
        etCardNumber = findViewById(R.id.vCardNumberInput)
        etExpireDate = findViewById(R.id.etExpireDate)
        etCvv = findViewById(R.id.etCvv)
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

            etCardNumber.setRadius(fieldCornerRadius.toPx)
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

                etCardNumber.setTypeface(typeface)
                etExpireDate.typeface = typeface
                etCvv.typeface = typeface
                btnSave.setTypeface(typeface)
            }
        }
    }

    private fun setupListeners() {
        etCardNumber.onTextChanged = {
            saveCardViewModel.onCardPanEntered(it)
        }

        etCardNumber.onTextChangedWithDebounce = {
            infoViewModel.onCardPanEntered(it)
        }

        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etExpireDate.doOnTextChanged { text, _, _, _ ->
            saveCardViewModel.onExpireDateEntered(text.toString().replace("/", ""))
        }

        etCvv.doOnTextChanged { text, _, _, _ ->
            saveCardViewModel.onCvvEntered(text.toString())
        }

        vToolbar.setNavigationOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    private fun observeData() {
        with(infoViewModel) {
            cardBrand.observe(this@BindCardActivity) {
                etCardNumber.setBrand(it)
            }

            cardEmitter.observe(this@BindCardActivity) {
                etCardNumber.setEmitter(it)
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

        if (state is PENDING) {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(
                LAUNCHER, WebViewLauncher(getString(R.string.toolbar_title_3ds), state.actionUrl)
            )

            startForResult.launch(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                saveCardViewModel.onSaveClicked(
                    etCardNumber.getCardNumber(),
                    etExpireDate.text.toString(),
                    etCvv.text.toString()
                )
            }
            else -> {
                onBackPressed()
            }
        }
    }

}