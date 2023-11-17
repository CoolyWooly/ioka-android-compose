package kz.ioka.android.ioka.presentation.flows.saveCard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.Configuration
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class SaveCardActivity : BaseActivity() {

    companion object {
        fun provideIntent(
            context: Context,
            customerToken: String, configuration: Configuration?,
        ): Intent {
            return Intent(context, SaveCardActivity::class.java).apply {
                putExtra(
                    LAUNCHER,
                    SaveCardLauncher(customerToken, configuration)
                )
            }
        }
    }

    private lateinit var fcvContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_save_card)

        bindViews()
        showForm()
    }

    private fun bindViews() {
        fcvContainer = findViewById(R.id.fcvContainer)
    }

    private fun showForm() {
        val launcher = launcher<SaveCardLauncher>()

        if (launcher != null)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack(null)
                add(R.id.fcvContainer, CardFormFragment.getInstance(launcher))
            }
        else
            finish()
    }
}