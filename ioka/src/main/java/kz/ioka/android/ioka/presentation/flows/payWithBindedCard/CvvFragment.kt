package kz.ioka.android.ioka.presentation.flows.payWithBindedCard

import android.content.DialogInterface
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
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherActivity
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.ResultFragment
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.uikit.IokaStateButton
import kz.ioka.android.ioka.util.shortPanMask
import kz.ioka.android.ioka.util.showErrorToast
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
                onSuccessPayment()
            } else if (it.resultCode == AppCompatActivity.RESULT_CANCELED) {
                onFailedPayment(getString(R.string.ioka_result_failed_payment_common_cause))
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
        ivCardType.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), viewModel.cardType.toCardType().cardTypeRes
            )
        )
        tvCardNumber.text = viewModel.cardNumber.shortPanMask()
        etCvv.requestFocus()
    }

    private fun observeViewModel() {
        viewModel.apply {
            payState.observe(viewLifecycleOwner) {
                processPaymentState(it)
            }
        }
    }

    private fun processPaymentState(state: PaymentState) {
        val buttonState: ButtonState = when (state) {
            PaymentState.DISABLED -> {
                ButtonState.Disabled
            }
            PaymentState.LOADING -> {
                ButtonState.Loading
            }
            else -> {
                ButtonState.Default
            }
        }

        btnContinue.setState(buttonState)
        etCvv.isEnabled = state != PaymentState.LOADING
        btnClose.isClickable = state != PaymentState.LOADING

        when (state) {
            PaymentState.SUCCESS -> {
                onSuccessPayment()
            }
            is PaymentState.FAILED -> {
                onFailedPayment(state.cause)
            }
            is PaymentState.ERROR -> {
                context?.showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
            }
            is PaymentState.PENDING -> {
                onActionRequired(state.actionUrl)
            }
        }
    }

    private fun onSuccessPayment() {
        dismiss()

        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra(
            BaseActivity.LAUNCHER,
            SuccessResultLauncher(
                subtitle = if (viewModel.order.externalId.isBlank()) ""
                else getString(
                    R.string.ioka_result_success_payment_subtitle,
                    viewModel.order.externalId
                ),
                amount = viewModel.order.amount
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
        val intent = WebViewActivity.provideIntent(
            requireContext(), PaymentConfirmationBehavior(
                url = actionUrl,
                customerToken = viewModel.customerToken,
                orderToken = viewModel.orderToken,
                paymentId = viewModel.paymentId
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (activity is PaymentLauncherActivity) {
            activity?.finish()
        }
    }

}