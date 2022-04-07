package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherActivity
import kz.ioka.android.ioka.presentation.result.ResultActivity
import kz.ioka.android.ioka.presentation.result.ResultFragment
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.uikit.ButtonState
import kz.ioka.android.ioka.uikit.IokaStateButton
import kz.ioka.android.ioka.uikit.TooltipWindow
import kz.ioka.android.ioka.util.shortPanMask
import kz.ioka.android.ioka.util.showErrorToast
import kz.ioka.android.ioka.util.toCardType
import kz.ioka.android.ioka.viewBase.BaseActivity
import kz.ioka.android.ioka.viewBase.BasePaymentFragment

internal class PayWithCvvFragment : BasePaymentFragment(R.layout.ioka_fragment_cvv),
    View.OnClickListener {

    companion object {
        const val LAUNCHER = "CvvFragment_LAUNCHER"

        fun newInstance(launcher: PayWithCvvLauncher): PayWithCvvFragment {
            val fragment = PayWithCvvFragment()

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
    private lateinit var vContainer: LinearLayoutCompat
    private lateinit var ivCardType: AppCompatImageView
    private lateinit var tvCardNumber: AppCompatTextView
    private lateinit var etCvv: AppCompatEditText
    private lateinit var ivCvvFaq: AppCompatImageButton
    private lateinit var btnContinue: IokaStateButton

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
        setConfiguration()
        setupListeners()
        setInitialData()
        observeViewModel()
    }

    private fun bindViews(root: View) {
        tipWindow = TooltipWindow(requireContext())

        vRoot = root.findViewById(R.id.vRoot)
        btnClose = root.findViewById(R.id.btnClose)
        vContainer = root.findViewById(R.id.vContainer)
        ivCardType = root.findViewById(R.id.ivCardType)
        tvCardNumber = root.findViewById(R.id.tvCardNumber)
        etCvv = root.findViewById(R.id.etCvv)
        ivCvvFaq = root.findViewById(R.id.ivCvvFaq)
        btnContinue = root.findViewById(R.id.btnContinue)
    }

    private fun setConfiguration() {
        arguments?.getParcelable<PayWithCvvLauncher>(LAUNCHER)?.configuration?.apply {
            vRoot.setBackgroundColor(
                ContextCompat.getColor(requireContext(), backgroundColor)
            )

            ImageViewCompat.setImageTintList(
                ivCvvFaq,
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), iconColor))
            )

            buttonText?.let { btnContinue.setText(buttonText) }

            fieldBackground?.let {
                vContainer.background = ContextCompat.getDrawable(requireContext(), it)
            }
            buttonBackground?.let {
                btnContinue.background = ContextCompat.getDrawable(requireContext(), it)
            }
        }
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
                onSuccessfulPayment()
            }
            is PaymentState.FAILED -> {
                onFailedPayment(state.cause)
            }
            is PaymentState.ERROR -> {
                context?.showErrorToast(state.cause ?: getString(R.string.ioka_common_server_error))
            }
            is PaymentState.PENDING -> {
                onActionRequired(
                    state.actionUrl,
                    viewModel.orderToken,
                    viewModel.paymentId
                )
            }
            else -> return
        }
    }

    override fun onSuccessfulPayment() {
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
        if (activity is PaymentLauncherActivity)
            activity?.finish()
    }

    override fun onFailedPayment(cause: String?) {
        activity?.supportFragmentManager?.popBackStack()

        val a = ResultFragment.newInstance(cause)
        a.show(requireActivity().supportFragmentManager, ResultFragment::class.simpleName)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnClose -> {
                dismiss()
                if (activity is PaymentLauncherActivity)
                    activity?.finish()
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