package kz.ioka.android.ioka.presentation.flows.payWithCard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.flows.payWithCard.PayWithCardViewModel
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModel
import kz.ioka.android.ioka.uikit.CardNumberEditText
import kz.ioka.android.ioka.uikit.StateButton
import kz.ioka.android.ioka.viewBase.BaseActivity

@AndroidEntryPoint
class PayWithCardActivity : BaseActivity() {

    private val viewModel: PayWithCardViewModel by viewModels()
    private val cardInfoViewModel: CardInfoViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar
    private lateinit var btnGooglePay: AppCompatImageButton
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpireDate: AppCompatEditText
    private lateinit var etCvv: AppCompatEditText
    private lateinit var switchBindCard: SwitchCompat
    private lateinit var btnPay: StateButton

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_with_card)

        bindViews()
        setupListeners()
        observeData()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        btnGooglePay = findViewById(R.id.btnGooglePay)
        etCardNumber = findViewById(R.id.vCardNumberInput)
        etExpireDate = findViewById(R.id.etExpireDate)
        etCvv = findViewById(R.id.etCvv)
        switchBindCard = findViewById(R.id.vBindCardSwitch)
        btnPay = findViewById(R.id.btnPay)
    }

    private fun observeData() {
        viewModel.apply {
            vToolbar.title = getString(R.string.pay_with_card_toolbar, price.toString().plus(" ₸"))
            btnPay.setText(getString(R.string.pay_with_amount, price.toString().plus(" ₸")))
        }
    }

    private fun setupListeners() {
        etCardNumber.onTextChangedWithDebounce = {
            cardInfoViewModel.onCardPanEntered(it)
        }
        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etCardNumber.onTextChanged = {
            viewModel.onCardPanEntered(it)
        }

        etExpireDate.doOnTextChanged { text, _, _, _ ->
            viewModel.onExpireDateEntered(text.toString().replace("/", ""))
        }
    }

}