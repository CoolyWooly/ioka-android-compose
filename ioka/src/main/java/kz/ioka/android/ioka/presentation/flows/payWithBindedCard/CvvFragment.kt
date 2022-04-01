package kz.ioka.android.ioka.presentation.flows.payWithBindedCard

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.payWithCard.PayState
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.ResultFragment
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.presentation.webView.WebViewLauncher
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.uikit.IokaStateButton
import kz.ioka.android.ioka.util.*
import kz.ioka.android.ioka.util.getOrderId
import kz.ioka.android.ioka.util.shortPanMask
import kz.ioka.android.ioka.util.toCardType
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class CvvFragment : DialogFragment(R.layout.fragment_cvv), View.OnClickListener {

    companion object {
        const val LAUNCHER = "CvvFragment_LAUNCHER"

        fun newInstance(launcher: CvvLauncher): CvvFragment {
            val fragment = CvvFragment()

            val arguments = Bundle()
            arguments.putParcelable(LAUNCHER, launcher)
            fragment.arguments = arguments

            return fragment
        }
    }

    private val viewModel: CvvViewModel by viewModels {
        CvvViewModelFactory(
            requireArguments().getParcelable(LAUNCHER)!!,
            PaymentRepositoryImpl(DependencyInjector.paymentApi)
        )
    }

    private lateinit var tipWindow: TooltipWindow

    private lateinit var vRoot: ConstraintLayout
    private lateinit var btnClose: AppCompatImageButton
    private lateinit var ivCardType: AppCompatImageView
    private lateinit var tvCardNumber: AppCompatTextView
    private lateinit var etCvv: AppCompatEditText
    private lateinit var ivCvvFaq: AppCompatImageButton
    private lateinit var btnContinue: IokaStateButton

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                viewModel.on3DSecurePassed()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupListeners()
        setInitialData()
        observeViewModel()
    }

    private fun bindViews(root: View) {
        tipWindow = TooltipWindow(requireContext())

        vRoot = root.findViewById(R.id.vRoot)
        btnClose = root.findViewById(R.id.btnClose)
        ivCardType = root.findViewById(R.id.ivCardType)
        tvCardNumber = root.findViewById(R.id.tvCardNumber)
        etCvv = root.findViewById(R.id.etCvv)
        ivCvvFaq = root.findViewById(R.id.ivCvvFaq)
        btnContinue = root.findViewById(R.id.btnContinue)
    }

    private fun setupListeners() {
        etCvv.doAfterTextChanged {
            viewModel.onCvvChanged(it.toString())
        }
        btnClose.setOnClickListener(this)
        ivCvvFaq.setOnClickListener(this)
        btnContinue.setOnClickListener(this)
    }

    private fun setInitialData() {
        val launcher = requireArguments().getParcelable<CvvLauncher>(LAUNCHER)!!

        ivCardType.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), launcher.cardType.toCardType().cardTypeRes
            )
        )
        tvCardNumber.text = launcher.cardNumber.shortPanMask()
        etCvv.requestFocus()
    }

    private fun observeViewModel() {
        viewModel.apply {
            payState.observe(viewLifecycleOwner) {
                processPayState(it)
            }
        }
    }

    private fun processPayState(state: PayState) {
        val buttonState: ButtonState = when (state) {
            PayState.DISABLED -> {
                ButtonState.Disabled
            }
            PayState.LOADING -> {
                ButtonState.Loading
            }
            else -> {
                ButtonState.Default
            }
        }

        btnContinue.setState(buttonState)
        etCvv.isEnabled = state != PayState.LOADING
        btnClose.isClickable = state != PayState.LOADING

        when (state) {
            PayState.SUCCESS -> {
                onSuccessPayment()
            }
            is PayState.FAILED -> {
                onFailedPayment(state.cause)
            }
            is PayState.ERROR -> {
                context?.showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
            }
            is PayState.PENDING -> {
                onActionRequired(state.actionUrl)
            }
        }
    }

    private fun onSuccessPayment() {
        dismiss()

        val launcher = requireArguments().getParcelable<CvvLauncher>(LAUNCHER)!!

        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra(
            BaseActivity.LAUNCHER,
            SuccessResultLauncher(
                subtitle = getString(
                    R.string.ioka_result_success_payment_subtitle,
                    launcher.orderToken.getOrderId()
                ),
                amount = launcher.price
            )
        )

        startActivity(intent)
    }

    private fun onFailedPayment(cause: String?) {
        dismiss()

        val a = ResultFragment.newInstance(cause)
        a.show(requireActivity().supportFragmentManager, ResultFragment::class.simpleName)
    }

    private fun onActionRequired(actionUrl: String) {
        val intent = Intent(requireContext(), WebViewActivity::class.java)
        intent.putExtra(
            BaseActivity.LAUNCHER,
            WebViewLauncher(
                getString(R.string.ioka_common_payment_confirmation),
                actionUrl
            )
        )
        startForResult.launch(intent)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnClose -> {
                dismiss()
            }

            ivCvvFaq -> {
                if (!tipWindow.isTooltipShown)
                    tipWindow.showToolTip(ivCvvFaq)
            }

            btnContinue -> {
                viewModel.onContinueClicked(etCvv.text.toString())
            }
        }
    }

}