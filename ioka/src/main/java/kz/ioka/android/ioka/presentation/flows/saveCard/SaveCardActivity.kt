package kz.ioka.android.ioka.presentation.flows.saveCard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.Configuration
import kz.ioka.android.ioka.api.FlowResult
import kz.ioka.android.ioka.api.IOKA_EXTRA_RESULT_NAME
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class SaveCardActivity : BaseActivity() {

    companion object {
        fun provideIntent(
            activity: Activity,
            customerToken: String, configuration: Configuration?,
        ): Intent {
            return Intent(activity, SaveCardActivity::class.java).apply {
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

    fun finishWithCanceledResult() {
        setResult(RESULT_CANCELED, Intent().apply {
            putExtra(IOKA_EXTRA_RESULT_NAME, FlowResult.Cancelled)
        })

        finish()
    }

    fun finishWithSucceededResult() {
        setResult(RESULT_OK, Intent().apply {
            putExtra(IOKA_EXTRA_RESULT_NAME, FlowResult.Succeeded)
        })

        finish()
    }
}