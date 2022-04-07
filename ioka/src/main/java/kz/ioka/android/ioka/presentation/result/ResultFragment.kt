package kz.ioka.android.ioka.presentation.result

import android.content.DialogInterface
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
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherActivity
import kz.ioka.android.ioka.uikit.IokaStateButton

class ResultFragment : DialogFragment(R.layout.ioka_fragment_result) {

    companion object {
        private const val LAUNCHER = "ResultFragment_LAUNCHER"

        fun newInstance(cause: String? = null): ResultFragment {
            val args = Bundle()
            args.putString(LAUNCHER, cause)

            val fragment = ResultFragment()
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

        if (activity is PaymentLauncherActivity)
            activity?.finish()
    }

}