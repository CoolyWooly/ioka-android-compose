package kz.ioka.android.ioka.presentation.launcher

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.viewBase.BaseFragment

internal class PaymentLauncherFragment : BaseFragment(R.layout.ioka_fragment_launcher) {

    companion object {
        internal fun getInstance(behavior: PaymentLauncherBehavior): PaymentLauncherFragment {
            val bundle = Bundle()
            bundle.putParcelable(FRAGMENT_LAUNCHER, behavior)

            val fragment = PaymentLauncherFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private val viewModel: PaymentLauncherViewModel by viewModels {
        PaymentLauncherViewModelFactory(launcher()!!)
    }

    private lateinit var vProgress: LinearLayoutCompat
    private lateinit var tvTitle: AppCompatTextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupViews()
    }

    private fun bindViews(view: View) {
        vProgress = view.findViewById(R.id.vProgress)
        tvTitle = view.findViewById(R.id.tvTitle)
    }

    private fun setupViews() {
        viewModel.apply {
            tvTitle.setText(titleRes)

            onUiShown()

            action.observe(viewLifecycleOwner) {
                it.invoke(requireActivity())
            }

            progress.observe(viewLifecycleOwner) {
                vProgress.isVisible = it
            }
        }
    }
}