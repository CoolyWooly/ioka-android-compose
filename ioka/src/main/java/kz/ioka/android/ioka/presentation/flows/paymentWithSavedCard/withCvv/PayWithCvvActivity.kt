package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.presentation.flows.saveCard.SaveCardLauncher
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherFragment
import kz.ioka.android.ioka.util.addFragment
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class PayWithCvvActivity : BaseActivity() {

    companion object {
        fun provideIntent(context: Context, launcher: CvvPaymentLauncher): Intent {
            val intent = Intent(context, PayWithCvvActivity::class.java)
            intent.putExtra(LAUNCHER, launcher)

            return intent
        }
    }

    private lateinit var fcvContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launcher = launcher<CvvPaymentLauncher>()
        if (launcher?.configuration?.themeId != null) {
            setTheme(launcher.configuration.themeId)
        }
        setContentView(R.layout.ioka_activity_pay_with_card_id)

        bindViews()
        showForm()
    }

    private fun bindViews() {
        fcvContainer = findViewById(R.id.fcvContainer)
    }

    private fun showForm() {
        val launcher = launcher<CvvPaymentLauncher>()

        launcher?.let {
            supportFragmentManager.addFragment(
                PaymentLauncherFragment.getInstance(
                    CvvPaymentLauncherBehavior(
                        launcher.orderToken,
                        launcher.cardDvo,
                        launcher.configuration
                    )
                )
            )
        }
    }
}