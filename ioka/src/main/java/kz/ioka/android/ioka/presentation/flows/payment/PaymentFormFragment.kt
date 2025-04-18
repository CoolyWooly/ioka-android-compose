package kz.ioka.android.ioka.presentation.flows.payment

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModel
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.presentation.result.ErrorResultLauncher
import kz.ioka.android.ioka.presentation.result.ResultFragment
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.ResultState
import kz.ioka.android.ioka.presentation.webView.WebViewFragment
import kz.ioka.android.ioka.uikit.*
import kz.ioka.android.ioka.util.addFragment
import kz.ioka.android.ioka.util.replaceFragment
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.util.toAmountFormat
import kz.ioka.android.ioka.viewBase.BaseActivity
import kz.ioka.android.ioka.viewBase.BaseFragment
import kz.ioka.android.ioka.viewBase.Scannable

internal class PaymentFormFragment : BaseFragment(R.layout.ioka_fragment_payment_form), Scannable,
    View.OnClickListener {

    companion object {

        const val TAG = "PAYMENT_FROM_FRAGMENT_TAG"

        internal fun getInstance(launcher: PaymentFormLauncher): PaymentFormFragment {
            val bundle = Bundle()
            bundle.putParcelable(FRAGMENT_LAUNCHER, launcher)

            val fragment = PaymentFormFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private val cardInfoViewModel: CardInfoViewModel by viewModels()

    private val payViewModel: PayWithCardViewModel by viewModels {
        PayWithCardViewModelFactory(
            launcher()!!,
            PaymentRepositoryImpl(DependencyInjector.paymentApi)
        )
    }

    private lateinit var tipWindow: TooltipWindow

    private lateinit var vRoot: ConstraintLayout
    private lateinit var vToolbar: Toolbar
    private lateinit var vToolbarTitle: AppCompatTextView
    private lateinit var groupGooglePay: Group
    private lateinit var btnGooglePay: AppCompatImageButton
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpiryDate: ExpiryDateEditText
    private lateinit var etCvv: CvvEditText
    private lateinit var switchSaveCard: SwitchCompat
    private lateinit var btnPay: IokaStateButton

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(WebViewFragment.WEB_VIEW_REQUEST_KEY) { _, bundle ->
            val result =
                bundle.getParcelable<ResultState>(WebViewFragment.WEB_VIEW_RESULT_BUNDLE_KEY)

            if (result is ResultState.Success) onSuccessfulAttempt()
            else if (result is ResultState.Fail) onFailedAttempt(result.cause)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setConfiguration()
        setupListeners()
        observeData()
    }

    override fun onResume() {
        super.onResume()

        enableInputs()
//        Commented this line because clients copied card data from another app, so it should not be deleted on app switch
//        clearInputs()
    }

    private fun bindViews(view: View) {
        tipWindow = TooltipWindow(requireContext())

        vRoot = view.findViewById(R.id.vRoot)
        vToolbar = view.findViewById(R.id.vToolbar)
        vToolbarTitle = view.findViewById(R.id.tvToolbarTitle)
        groupGooglePay = view.findViewById(R.id.groupGooglePay)
        btnGooglePay = view.findViewById(R.id.btnGooglePay)
        etCardNumber = view.findViewById(R.id.vCardNumberInput)
        etExpiryDate = view.findViewById(R.id.etExpiryDate)
        etCvv = view.findViewById(R.id.etCvvInput)
        switchSaveCard = view.findViewById(R.id.vSaveCardSwitch)
        btnPay = view.findViewById(R.id.btnPay)
    }

    private fun setConfiguration() {
        launcher<PaymentFormLauncher>()?.configuration?.apply {
            vRoot.setBackgroundColor(
                ContextCompat.getColor(requireContext(), backgroundColor)
            )

            etCardNumber.setIconColor(iconColor)
            etCvv.setIconColor(iconColor)

            buttonText?.let { btnPay.setText(buttonText) }

            fieldBackground?.let {
                etCardNumber.background = ContextCompat.getDrawable(requireContext(), it)
                etExpiryDate.background = ContextCompat.getDrawable(requireContext(), it)
                etCvv.background = ContextCompat.getDrawable(requireContext(), it)
            }
            buttonBackground?.let {
                btnPay.background = ContextCompat.getDrawable(requireContext(), it)
            }
        }
    }

    private fun setupListeners() {
        vToolbar.setNavigationOnClickListener(this)
        btnPay.setOnClickListener(this)

        etCardNumber.onTextChangedWithDebounce = {
            cardInfoViewModel.onCardPanEntered(it)
        }
        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etCardNumber.onScanClicked = {
            startCardScanner(this)
        }

        etCvv.onFaqClicked = {
            tipWindow.showToolTip(etCvv.ivCvvFaq)
        }
    }

    private fun observeData() {
        with(cardInfoViewModel) {
            cardNumberLength.observe(viewLifecycleOwner) {
                etCardNumber.setCardNumberLengthRange(it)
            }

            cardBrand.observe(viewLifecycleOwner) {
                etCardNumber.setBrand(it)
            }

            cardEmitter.observe(viewLifecycleOwner) {
                etCardNumber.setEmitter(it)
            }
        }

        payViewModel.apply {
            vToolbarTitle.text =
                getString(R.string.ioka_payment_toolbar, order.amount.amount.toAmountFormat())

            if (launcher<PaymentFormLauncher>()?.configuration?.buttonText == null)
                btnPay.setText(
                    getString(
                        R.string.ioka_payment_button,
                        order.amount.amount.toAmountFormat()
                    )
                )
            groupGooglePay.isVisible = withGooglePay
            switchSaveCard.isVisible = canSaveCard

            isPayAvailable.observe(viewLifecycleOwner) {
                btnPay.isEnabled = it
                btnPay.isClickable = it
                btnPay.isFocusable = it
            }

            payState.observe(viewLifecycleOwner) {
                handleState(it)
            }
        }

        etCardNumber.isValid.observe(viewLifecycleOwner) {
            payViewModel.setIsCardNumberValid(it)
        }
        etExpiryDate.isValid.observe(viewLifecycleOwner) {
            payViewModel.setIsExpiryDateValid(it)
        }
        etCvv.isValid.observe(viewLifecycleOwner) {
            payViewModel.setIsCvvValid(it)
        }
    }

    private fun handleState(state: PaymentState) {
        when (state) {
            PaymentState.LOADING -> {
                btnPay.setState(ButtonState.Loading)
                disableInputs()
            }

            is PaymentState.PENDING -> {
                btnPay.setState(ButtonState.Default)

                parentFragmentManager.addFragment(
                    WebViewFragment.getInstance(
                        PaymentConfirmationBehavior(
                            url = state.actionUrl,
                            orderToken = payViewModel.orderToken,
                            paymentId = payViewModel.paymentId
                        )
                    )
                )
            }

            PaymentState.SUCCESS -> {
                btnPay.setState(ButtonState.Default)

                onSuccessfulAttempt()
            }

            is PaymentState.ERROR -> {
                btnPay.setState(ButtonState.Default)

                requireContext().showErrorToast(
                    when (state.cause) {
                        "NetworkError" -> {
                            getString(R.string.ioka_common_network_error)
                        }
                        "HttpError" -> {
                            getString(R.string.ioka_common_server_error)
                        }
                        else -> {
                            getString(R.string.ioka_common_server_error)
                        }
                    }
                )
            }

            is PaymentState.FAILED -> {
                btnPay.setState(ButtonState.Default)

                onFailedAttempt(
                    state.cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
                )
                enableInputs()
                clearInputs()
            }

            PaymentState.DEFAULT -> {
                btnPay.setState(ButtonState.Default)
                enableInputs()
            }

            PaymentState.DISABLED -> {}
        }
    }

    private fun enableInputs() {
        etCardNumber.isEnabled = true
        etExpiryDate.isEnabled = true
        etCvv.isEnabled = true
        switchSaveCard.isEnabled = true
        btnGooglePay.isEnabled = true
    }

    private fun disableInputs() {
        etCardNumber.isEnabled = false
        etExpiryDate.isEnabled = false
        etCvv.isEnabled = false
        switchSaveCard.isEnabled = false
        btnGooglePay.isEnabled = false
    }

    private fun clearInputs() {
        etCardNumber.clear()
        etExpiryDate.clear()
        etCvv.clear()
        switchSaveCard.isChecked = false
    }

    private fun onSuccessfulAttempt() {
        parentFragmentManager.replaceFragment(
            ResultFragment.getInstance(
                SuccessResultLauncher(
                    subtitle = getString(
                        R.string.ioka_result_success_payment_subtitle,
                        payViewModel.order.externalId
                    ),
                    amount = payViewModel.order.amount
                )
            )
        )
    }

    private fun onFailedAttempt(cause: String?) {
        parentFragmentManager.addFragment(
            ResultFragment.getInstance(
                ErrorResultLauncher(
                    subtitle = cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
                )
            )
        )
    }

    private fun onBackPressed() {
        (activity as? BaseActivity)?.finishWithCanceledResult()
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPay -> {
                payViewModel.onPayClicked(
                    etCardNumber.getCardNumber(),
                    etExpiryDate.getExpiryDate(),
                    etCvv.getCvv(),
                    switchSaveCard.isChecked
                )
            }

            else -> {
                onBackPressed()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<Scannable>.onActivityResult(requestCode, resultCode, data)
    }

}