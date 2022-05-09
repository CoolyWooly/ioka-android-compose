package kz.ioka.android.ioka.presentation.result

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.FlowResult
import kz.ioka.android.ioka.api.IOKA_EXTRA_RESULT_NAME
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherActivity
import kz.ioka.android.ioka.uikit.IokaStateButton

internal class FailedResultFragment : DialogFragment(R.layout.ioka_fragment_failed_result) {

    companion object {
        private const val LAUNCHER = "ResultFragment_LAUNCHER"

        fun newInstance(cause: String? = null): FailedResultFragment {
            val args = Bundle()
            args.putString(LAUNCHER, cause)

            val fragment = FailedResultFragment()
            fragment.arguments = args

            return fragment
        }
    }

    private val cause by lazy { requireArguments().getString(LAUNCHER) }

    private lateinit var tvStateSubTitle: AppCompatTextView
    private lateinit var btnTryAgain: IokaStateButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupViews()
    }

    private fun bindViews(root: View) {
        tvStateSubTitle = root.findViewById(R.id.tvStateSubTitle)
        btnTryAgain = root.findViewById(R.id.btnTryAgain)
    }

    private fun setupViews() {
        tvStateSubTitle.text = cause ?: getString(R.string.ioka_result_failed_payment_common_cause)

        btnTryAgain.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (activity is PaymentLauncherActivity) {
            activity?.setResult(
                RESULT_OK,
                Intent().putExtra(
                    IOKA_EXTRA_RESULT_NAME,
                    FlowResult.Failed(
                        cause ?: getString(R.string.ioka_result_failed_payment_common_cause)
                    )
                )
            )
            activity?.finish()
        }
    }

}