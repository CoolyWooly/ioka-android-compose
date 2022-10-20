package kz.ioka.android.ioka.presentation.flows.saveCard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.presentation.flows.common.CardInfoViewModel
import kz.ioka.android.ioka.presentation.webView.ResultState
import kz.ioka.android.ioka.presentation.webView.SaveCardConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewFragment
import kz.ioka.android.ioka.presentation.webView.WebViewFragment.Companion.WEB_VIEW_REQUEST_KEY
import kz.ioka.android.ioka.presentation.webView.WebViewFragment.Companion.WEB_VIEW_RESULT_BUNDLE_KEY
import kz.ioka.android.ioka.uikit.*
import kz.ioka.android.ioka.util.addFragment
import kz.ioka.android.ioka.viewBase.BaseFragment
import kz.ioka.android.ioka.viewBase.Scannable

class CardFormFragment : BaseFragment(R.layout.ioka_fragment_card_form),
    View.OnClickListener, Scannable {

    companion object {
        internal fun getInstance(launcher: SaveCardLauncher): CardFormFragment {
            val bundle = Bundle()
            bundle.putParcelable(FRAGMENT_LAUNCHER, launcher)

            val fragment = CardFormFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val cardInfoViewModel: CardInfoViewModel by viewModels()

    private val saveCardViewModel: SaveCardViewModel by viewModels {
        SaveCardViewModelFactory(launcher()!!)
    }

    private lateinit var tipWindow: TooltipWindow

    private lateinit var vRoot: LinearLayoutCompat
    private lateinit var vToolbar: Toolbar
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpiryDate: ExpiryDateEditText
    private lateinit var vCvvInput: CvvEditText
    private lateinit var vError: ErrorView
    private lateinit var btnSave: IokaStateButton

    override fun onCardScanned(cardNumber: String) {
        etCardNumber.setCardNumber(cardNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(WEB_VIEW_REQUEST_KEY) { _, bundle ->
            val result = bundle.getParcelable<ResultState>(WEB_VIEW_RESULT_BUNDLE_KEY)

            if (result is ResultState.Success) onSuccessfulAttempt()
            else if (result is ResultState.Fail) onFailedAttempt(result.cause)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setConfiguration()
        setupListeners()
        setupViews()
        observeData()
    }

    private fun bindViews(view: View) {
        tipWindow = TooltipWindow(requireContext())

        vRoot = view.findViewById(R.id.vRoot)
        vToolbar = view.findViewById(R.id.vToolbar)
        etCardNumber = view.findViewById(R.id.vCardNumberInput)
        etExpiryDate = view.findViewById(R.id.etExpiryDate)
        vCvvInput = view.findViewById(R.id.vCvvInput)
        vError = view.findViewById(R.id.vError)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun setConfiguration() {
        launcher<SaveCardLauncher>()?.configuration?.apply {
            vRoot.setBackgroundColor(
                ContextCompat.getColor(requireContext(), backgroundColor)
            )

            etCardNumber.setIconColor(iconColor)
            vCvvInput.setIconColor(iconColor)

            buttonText?.let { btnSave.setText(buttonText) }

            fieldBackground?.let {
                etCardNumber.background = ContextCompat.getDrawable(requireContext(), it)
                etExpiryDate.background = ContextCompat.getDrawable(requireContext(), it)
                vCvvInput.background = ContextCompat.getDrawable(requireContext(), it)
            }
            buttonBackground?.let {
                btnSave.background = ContextCompat.getDrawable(requireContext(), it)
            }
        }
    }

    private fun setupViews() {
        btnSave.setCallback(object : ResultCallback {
            override fun onSuccess(): () -> Unit = {
                doAfterSuccess()
            }
        })
    }

    private fun doAfterSuccess() {
        lifecycleScope.launch {
            delay(500)

            (activity as? SaveCardActivity)?.finishWithSucceededResult()
        }
    }

    private fun setupListeners() {
        vToolbar.setNavigationOnClickListener(this)
        btnSave.setOnClickListener(this)

        etCardNumber.onTextChangedWithDebounce = {
            cardInfoViewModel.onCardPanEntered(it)
        }
        etCardNumber.flowTextChangedWithDebounce.launchIn(lifecycleScope)

        etCardNumber.onScanClicked = {
            startCardScanner(this)
        }

        vCvvInput.onFaqClicked = {
            tipWindow.showToolTip(vCvvInput.ivCvvFaq)
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

        saveCardViewModel.apply {
            isPayAvailable.observe(viewLifecycleOwner) {
                btnSave.isEnabled = it
                btnSave.isClickable = it
                btnSave.isFocusable = it
            }

            saveRequestState.observe(viewLifecycleOwner) {
                handleState(it)
            }
        }

        etCardNumber.isValid.observe(viewLifecycleOwner) {
            saveCardViewModel.setIsCardNumberValid(it)
        }
        etExpiryDate.isValid.observe(viewLifecycleOwner) {
            saveCardViewModel.setIsExpiryDateValid(it)
        }
        vCvvInput.isValid.observe(viewLifecycleOwner) {
            saveCardViewModel.setIsCvvValid(it)
        }
    }

    private fun handleState(state: SaveCardRequestState) {
        when (state) {
            SaveCardRequestState.LOADING -> {
                btnSave.setState(ButtonState.Loading)
            }

            is SaveCardRequestState.PENDING -> {
                btnSave.setState(ButtonState.Default)
            }

            SaveCardRequestState.SUCCESS -> {
                btnSave.setState(ButtonState.Success)
            }

            is SaveCardRequestState.ERROR -> {
                btnSave.setState(ButtonState.Default)
            }

            SaveCardRequestState.DEFAULT -> {
                btnSave.setState(ButtonState.Default)
            }
        }

//        val buttonState = when (state) {
//            SaveCardRequestState.SUCCESS -> ButtonState.Success
//
//            SaveCardRequestState.LOADING -> ButtonState.Loading
//
//            else -> ButtonState.Default
//        }
//
//        btnSave.setState(buttonState)

        etCardNumber.isEnabled = state !is SaveCardRequestState.LOADING
        etExpiryDate.isEnabled = state !is SaveCardRequestState.LOADING
        vCvvInput.isEnabled = state !is SaveCardRequestState.LOADING

        if (state is SaveCardRequestState.PENDING) {
            parentFragmentManager.addFragment(
                WebViewFragment.getInstance(
                    SaveCardConfirmationBehavior(
                        url = state.actionUrl,
                        customerToken = saveCardViewModel.customerToken,
                        cardId = saveCardViewModel.cardId!!
                    )
                )
            )
        } else if (state is SaveCardRequestState.ERROR) {
            onFailedAttempt(state.cause ?: getString(R.string.ioka_common_server_error))
        }
    }

    private fun onSuccessfulAttempt() {
        btnSave.setState(ButtonState.Success)
    }

    private fun onFailedAttempt(cause: String?) {
        vError.show(cause)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                saveCardViewModel.onSaveClicked(
                    etCardNumber.getCardNumber(),
                    etExpiryDate.text.toString(),
                    vCvvInput.getCvv()
                )
            }

            else -> {
                onBackPressed()
            }
        }
    }

    private fun onBackPressed() {
        (activity as? SaveCardActivity)?.finishWithCanceledResult()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<BaseFragment>.onActivityResult(requestCode, resultCode, data)
        super<Scannable>.onActivityResult(requestCode, resultCode, data)
    }

}