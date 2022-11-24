package kz.ioka.android.ioka.presentation.result

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.util.getDrawableFromRes
import kz.ioka.android.ioka.util.toAmountFormat
import kz.ioka.android.ioka.viewBase.BaseActivity
import kz.ioka.android.ioka.viewBase.BaseFragment
import java.math.BigDecimal

internal class ResultFragment : BaseFragment(R.layout.ioka_fragment_result) {

    companion object {
        internal fun getInstance(launcher: ResultLauncher): ResultFragment {
            val bundle = Bundle()
            bundle.putParcelable(FRAGMENT_LAUNCHER, launcher)

            val fragment = ResultFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var launcher: ResultLauncher

    lateinit var vToolbar: Toolbar
    lateinit var ivStatus: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var tvSubtitle: AppCompatTextView
    lateinit var tvAmount: AppCompatTextView
    lateinit var btnAction: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launcher = launcher()!!
        bindViews(view)
        setData()
        setupListeners()
    }

    private fun bindViews(view: View) {
        vToolbar = view.findViewById(R.id.vToolbar)
        ivStatus = view.findViewById(R.id.ivStatus)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvSubtitle = view.findViewById(R.id.tvSubtitle)
        tvAmount = view.findViewById(R.id.tvAmount)
        btnAction = view.findViewById(R.id.btnAction)
    }

    private fun setData() {
        launcher.let {
            ivStatus.setImageDrawable(requireContext().getDrawableFromRes(it.statusIconRes))
            tvTitle.setText(it.titleRes)
            tvTitle.setTextColor(ContextCompat.getColor(requireContext(), it.titleColorRes))
            tvSubtitle.text = it.subtitle
            btnAction.setText(it.btnTitleRes)

            if (it.amount.amount != BigDecimal.ZERO)
                tvAmount.text = it.amount.amount.toAmountFormat()
        }
    }

    private fun setupListeners() {
        vToolbar.setNavigationOnClickListener {
            finishWithResult()
        }

        btnAction.setOnClickListener {
            if (launcher.isTryAgainAvailable) {
                parentFragmentManager.popBackStack()
            } else {
                finishWithResult()
            }
        }
    }

    private fun finishWithResult() {
        (activity as? BaseActivity)?.finishWithResult(RESULT_OK, launcher.flowResult)
    }

}