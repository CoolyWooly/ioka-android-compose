package kz.ioka.android.ioka.presentation.flows.saveCard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepositoryImpl
import kz.ioka.android.ioka.domain.saveCard.CardRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModel
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModelFactory
import kz.ioka.android.ioka.presentation.flows.saveCard.SaveCardRequestState.*
import kz.ioka.android.ioka.presentation.webView.SaveCardConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.presentation.webView.WebViewActivity.Companion.EXTRA_FAIL_CAUSE
import kz.ioka.android.ioka.presentation.webView.WebViewActivity.Companion.RESULT_FAIL
import kz.ioka.android.ioka.presentation.webView.WebViewActivity.Companion.RESULT_SUCCESS
import kz.ioka.android.ioka.uikit.*
import kz.ioka.android.ioka.util.getStringExtra
import kz.ioka.android.ioka.viewBase.BaseActivity
import kz.ioka.android.ioka.viewBase.Scannable

internal class SaveCardActivity : BaseActivity(), View.OnClickListener, Scannable {

    private val infoViewModel: CardInfoViewModel by viewModels {
        CardInfoViewModelFactory(
            CardInfoRepositoryImpl(DependencyInjector.cardInfoApi)
        )
    }
    private val saveCardViewModel: SaveCardViewModel by viewModels {
        SaveCardViewModelFactory(
            launcher()!!,
            CardRepositoryImpl(DependencyInjector.cardApi)
        )
    }

    private lateinit var tipWindow: TooltipWindow
    private lateinit var vRoot: LinearLayoutCompat
    private lateinit var vToolbar: Toolbar
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpireDate: AppCompatEditText
    private lateinit var vCvvInput: CvvEditText
    private lateinit var vError: ErrorView
    private lateinit var btnSave: IokaStateButton

    private val resultForThreeDSecure =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_SUCCESS) {
                onSuccessAttempt()
            } else if (it.resultCode == RESULT_FAIL) {
                onFailedAttempt(
                    it.getStringExtra(
                        EXTRA_FAIL_CAUSE, getString(R.string.ioka_common_server_error)
                    )
                )
            }
        }

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_save_card)

        bindViews()
        setConfiguration()
        setupListeners()
        setupViews()
        observeData()
    }

    private fun bindViews() {
        tipWindow = TooltipWindow(this)
        vRoot = findViewById(R.id.vRoot)
        vToolbar = findViewById(R.id.vToolbar)
        etCardNumber = findViewById(R.id.vCardNumberInput)
        etExpireDate = findViewById(R.id.etExpireDate)
        vCvvInput = findViewById(R.id.vCvvInput)
        vError = findViewById(R.id.vError)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun setConfiguration() {
        launcher<SaveCardLauncher>()?.configuration?.apply {
            vRoot.setBackgroundColor(
                ContextCompat.getColor(this@SaveCardActivity, backgroundColor)
            )

            etCardNumber.setIconColor(iconColor)
            vCvvInput.setIconColor(iconColor)

            btnSave.setText(buttonText ?: getString(R.string.ioka_save_card_save))

            fieldBackground?.let {
                etCardNumber.background = ContextCompat.getDrawable(this@SaveCardActivity, it)
                etExpireDate.background = ContextCompat.getDrawable(this@SaveCardActivity, it)
                vCvvInput.background = ContextCompat.getDrawable(this@SaveCardActivity, it)
            }
            buttonBackground?.let {
                btnSave.background = ContextCompat.getDrawable(this@SaveCardActivity, it)
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
            saveCardViewModel.onCardPanEntered(it)
        }

        etCardNumber.onTextChangedWithDebounce = {
            infoViewModel.onCardPanEntered(it)
        }

        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etExpireDate.doOnTextChanged { text, _, _, _ ->
            saveCardViewModel.onExpireDateEntered(text.toString().replace("/", ""))
        }

        vCvvInput.onTextChanged = {
            saveCardViewModel.onCvvEntered(it)
        }

        vCvvInput.onFaqClicked = {
            tipWindow.showToolTip(vCvvInput.ivCvvFaq)
        }

        vToolbar.setNavigationOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    private fun observeData() {
        with(infoViewModel) {
            cardBrand.observe(this@SaveCardActivity) {
                etCardNumber.setBrand(it)
            }

            cardEmitter.observe(this@SaveCardActivity) {
                etCardNumber.setEmitter(it)
            }
        }

        with(saveCardViewModel) {
            saveRequestState.observe(this@SaveCardActivity) {
                handleState(it)
            }
        }
    }

    private fun handleState(state: SaveCardRequestState) {
        val buttonState = when (state) {
            SUCCESS -> ButtonState.Success

            LOADING -> ButtonState.Loading

            DISABLED -> ButtonState.Disabled

            else -> ButtonState.Default
        }

        btnSave.setState(buttonState)

        etCardNumber.isEnabled = state !is LOADING
        etExpireDate.isEnabled = state !is LOADING
        vCvvInput.isEnabled = state !is LOADING

        if (state is PENDING) {
            onActionRequired(state.actionUrl)
        } else if (state is ERROR) {
            onFailedAttempt(state.cause ?: getString(R.string.ioka_common_server_error))
        }
    }

    private fun onSuccessAttempt() {
        btnSave.setState(ButtonState.Success)
    }

    private fun onFailedAttempt(cause: String) {
        vError.show(cause)
    }

    private fun onActionRequired(actionUrl: String) {
        val intent = WebViewActivity.provideIntent(
            this, SaveCardConfirmationBehavior(
                toolbarTitleRes = R.string.ioka_common_payment_confirmation,
                url = actionUrl,
                customerToken = saveCardViewModel.customerToken,
                cardId = saveCardViewModel.cardId ?: ""
            )
        )

        resultForThreeDSecure.launch(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                saveCardViewModel.onSaveClicked(
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
        super<Scannable>.onActivityResult(requestCode, resultCode, data)
    }

}