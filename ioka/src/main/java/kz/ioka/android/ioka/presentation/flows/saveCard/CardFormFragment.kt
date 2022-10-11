package kz.ioka.android.ioka.presentation.flows.saveCard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
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

    private val infoViewModel: CardInfoViewModel by viewModels()

    private val saveCardViewModel: SaveCardViewModel by viewModels {
        SaveCardViewModelFactory(launcher()!!)
    }

    private lateinit var tipWindow: TooltipWindow
    private lateinit var vRoot: LinearLayoutCompat
    private lateinit var vToolbar: Toolbar
    private lateinit var etCardNumber: CardNumberEditText
    private lateinit var etExpireDate: AppCompatEditText
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
        etExpireDate = view.findViewById(R.id.etExpireDate)
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

            btnSave.setText(buttonText ?: getString(R.string.ioka_save_card_save))

            fieldBackground?.let {
                etCardNumber.background = ContextCompat.getDrawable(requireContext(), it)
                etExpireDate.background = ContextCompat.getDrawable(requireContext(), it)
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
        etCardNumber.onTextChanged = {
            saveCardViewModel.onCardPanEntered(it)
        }

        etCardNumber.onScanClicked = {
            startCardScanner(this)
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
            cardBrand.observe(viewLifecycleOwner) {
                etCardNumber.setBrand(it)
            }

            cardNumberLength.observe(viewLifecycleOwner) {
                etCardNumber.setCardNumberLengthRange(it)
                saveCardViewModel.onCardPanLengthReceived(it)
            }

            cardEmitter.observe(viewLifecycleOwner) {
                etCardNumber.setEmitter(it)
            }
        }

        saveCardViewModel.apply {
            saveRequestState.observe(viewLifecycleOwner) {
                handleState(it)
            }
        }
    }

    private fun handleState(state: SaveCardRequestState) {
        val buttonState = when (state) {
            SaveCardRequestState.SUCCESS -> ButtonState.Success

            SaveCardRequestState.LOADING -> ButtonState.Loading

            SaveCardRequestState.DISABLED -> ButtonState.Disabled

            else -> ButtonState.Default
        }

        btnSave.setState(buttonState)

        etCardNumber.isEnabled = state !is SaveCardRequestState.LOADING
        etExpireDate.isEnabled = state !is SaveCardRequestState.LOADING
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
                    etExpireDate.text.toString(),
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