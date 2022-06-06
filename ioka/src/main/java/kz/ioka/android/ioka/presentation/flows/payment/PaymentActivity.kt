package kz.ioka.android.ioka.presentation.flows.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.Configuration
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherFragment
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class PaymentActivity : BaseActivity() {

    companion object {
        fun provideIntent(
            activity: Activity,
            customerToken: String,
            configuration: Configuration?,
        ): Intent {
            return Intent(activity, PaymentActivity::class.java).apply {
                putExtra(
                    LAUNCHER,
                    PaymentLauncher(customerToken, configuration)
                )
            }
        }
    }

    private lateinit var fcvContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_payment)

        bindViews()
        showForm()
    }

    private fun bindViews() {
        fcvContainer = findViewById(R.id.fcvContainer)
    }

    private fun showForm() {
        val launcher = launcher<PaymentLauncher>()

        if (launcher != null)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack(null)
                add(
                    R.id.fcvContainer,
                    PaymentLauncherFragment.getInstance(
                        PaymentLauncherBehavior(launcher.orderToken, false, launcher.configuration)
                    )
                )
            }
        else
            finish()
    }

}