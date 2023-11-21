package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.presentation.flows.saveCard.SaveCardLauncher
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherFragment
import kz.ioka.android.ioka.util.addFragment
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class PayWithCardIdActivity : BaseActivity() {

    companion object {
        fun provideIntent(context: Context, launcher: PayWithCardIdLauncher): Intent {
            val intent = Intent(context, PayWithCardIdActivity::class.java)
            intent.putExtra(LAUNCHER, launcher)

            return intent
        }
    }

    private lateinit var fcvContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launcher = launcher<SaveCardLauncher>()
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
        val launcher = launcher<PayWithCardIdLauncher>()

        launcher?.let {
            supportFragmentManager.addFragment(
                PaymentLauncherFragment.getInstance(
                    CardIdPaymentLauncherBehavior(
                        launcher.orderToken,
                        launcher.cardId
                    )
                )
            )
        }
    }
}