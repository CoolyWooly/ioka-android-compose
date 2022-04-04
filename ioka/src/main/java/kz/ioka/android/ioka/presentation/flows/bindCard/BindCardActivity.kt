package kz.ioka.android.ioka.presentation.flows.bindCard

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.bindCard.CardRepositoryImpl
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.bindCard.BindCardRequestState.*
import kz.ioka.android.ioka.presentation.flows.bindCard.Configuration.Companion.DEFAULT_FONT
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModel
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModelFactory
import kz.ioka.android.ioka.presentation.flows.payWithBindedCard.TooltipWindow
import kz.ioka.android.ioka.presentation.webView.CardBindingConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.uikit.*
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.util.toPx
import kz.ioka.android.ioka.viewBase.BaseActivity
import kz.ioka.android.ioka.viewBase.Scanable

internal class BindCardActivity : BaseActivity(), View.OnClickListener, Scanable {

    private val infoViewModel: CardInfoViewModel by viewModels {
        CardInfoViewModelFactory(
            CardInfoRepositoryImpl(DependencyInjector.cardInfoApi)
        )
    }
    private val bindCardViewModel: BindCardViewModel by viewModels {
        BindCardViewModelFactory(
            launcher()!!,
            CardRepositoryImpl(DependencyInjector.cardApi)
        )
    }

    private lateinit var tipWindow: TooltipWindow
    private lateinit var vToolbar: Toolbar
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpireDate: AppCompatEditText
    private lateinit var vCvvInput: CvvEditText
    private lateinit var vGap: View
    private lateinit var btnSave: IokaStateButton

    private val resultFor3DSecure =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                btnSave.setState(ButtonState.Success)
            } else if (it.resultCode == RESULT_CANCELED) {
                showErrorToast(getString(R.string.ioka_common_server_error))
            }
        }

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_card)

        bindViews()
        setConfiguration()
        setupListeners()
        setupViews()
        observeData()
    }

    private fun bindViews() {
        tipWindow = TooltipWindow(this)
        vToolbar = findViewById(R.id.vToolbar)
        etCardNumber = findViewById(R.id.vCardNumberInput)
        etExpireDate = findViewById(R.id.etExpireDate)
        vCvvInput = findViewById(R.id.vCvvInput)
        vGap = findViewById(R.id.vGap)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun setConfiguration() {
        launcher<BindCardLauncher>()?.configuration?.apply {
            toolbarTitle?.let { vToolbar.title = it }

            etCardNumber.setRadius(fieldCornerRadius.toPx)
            (etExpireDate.background as GradientDrawable).cornerRadius = fieldCornerRadius.toPx
            vCvvInput.setRadius(fieldCornerRadius.toPx)

            btnSave.setConfiguration(
                bindButtonCornerRadius,
                bindButtonBackgroundColorRes,
                bindButtonTextRes ?: getString(R.string.ioka_bind_card_save)
            )

            if (fontRes != DEFAULT_FONT) {
                val typeface = ResourcesCompat.getFont(this@BindCardActivity, fontRes)
                checkNotNull(typeface)

                etCardNumber.setTypeface(typeface)
                etExpireDate.typeface = typeface
                vCvvInput.setTypeface(typeface)
                btnSave.setTypeface(typeface)
            }
        }
    }

    private fun setupViews() {
        btnSave.setCallback(object : Callback {
            override fun onSuccess(): () -> Unit = {
                lifecycleScope.launch {
                    delay(500)
                    finish()
                }
            }
        })
    }

    private fun setupListeners() {
        etCardNumber.onTextChanged = {
            bindCardViewModel.onCardPanEntered(it)
        }

        etCardNumber.onTextChangedWithDebounce = {
            infoViewModel.onCardPanEntered(it)
        }

        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etExpireDate.doOnTextChanged { text, _, _, _ ->
            bindCardViewModel.onExpireDateEntered(text.toString().replace("/", ""))
        }

        vCvvInput.onTextChanged = {
            bindCardViewModel.onCvvEntered(it)
        }

        vCvvInput.onFaqClicked = {
            if (!tipWindow.isTooltipShown)
                tipWindow.showToolTip(vCvvInput)
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

        with(bindCardViewModel) {
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
            showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
        }

        etCardNumber.isEnabled = state !is LOADING
        etExpireDate.isEnabled = state !is LOADING
        vCvvInput.isEnabled = state !is LOADING

        if (state is PENDING) {
            val intent = WebViewActivity.provideIntent(
                this, CardBindingConfirmationBehavior(
                    toolbarTitleRes = R.string.ioka_common_payment_confirmation,
                    url = state.actionUrl,
                    customerToken = bindCardViewModel.customerToken,
                    cardId = bindCardViewModel.cardId ?: ""
                )
            )

            resultFor3DSecure.launch(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                bindCardViewModel.onBindClicked(
                    etCardNumber.getCardNumber(),
                    etExpireDate.text.toString(),
                    vCvvInput.getCvv()
                )
            }
            else -> {
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<BaseActivity>.onActivityResult(requestCode, resultCode, data)
        super<Scanable>.onActivityResult(requestCode, resultCode, data)
    }

}