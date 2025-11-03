package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.ioka_activity_pay_with_card_id)
        applyEdgeToEdgeInsets()

        bindViews()
        showForm()
    }

    private fun applyEdgeToEdgeInsets() {
        val root = findViewById<View>(R.id.fcvContainer)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sysBars = insets.getInsets(WindowInsets.Type.systemBars()) // status + nav
            v.updatePadding(
                left = sysBars.left,
                top = sysBars.top,
                right = sysBars.right,
                bottom = sysBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
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