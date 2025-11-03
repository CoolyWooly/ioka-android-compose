package kz.ioka.android.ioka.presentation.flows.payment

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
import androidx.fragment.app.commit
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.Configuration
import kz.ioka.android.ioka.presentation.flows.saveCard.SaveCardLauncher
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherFragment
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class PaymentActivity : BaseActivity() {

    companion object {
        fun provideIntent(
            context: Context,
            customerToken: String,
            configuration: Configuration?,
        ): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
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
        val launcher = launcher<PaymentLauncher>()
        if (launcher?.configuration?.themeId != null) {
            setTheme(launcher.configuration.themeId)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.ioka_activity_payment)
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