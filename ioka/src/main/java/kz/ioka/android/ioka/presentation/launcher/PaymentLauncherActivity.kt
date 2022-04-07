package kz.ioka.android.ioka.presentation.launcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.viewBase.BaseActivity

internal class PaymentLauncherActivity : BaseActivity() {

    companion object {
        fun provideIntent(context: Context, behavior: PaymentLauncherBehavior): Intent {
            val intent = Intent(context, PaymentLauncherActivity::class.java)

            intent.putExtra(LAUNCHER, behavior)

            return intent
        }
    }

    private val viewModel: PaymentLauncherViewModel by viewModels {
        PaymentLauncherViewModelFactory(launcher()!!)
    }

    private lateinit var vProgress: LinearLayoutCompat
    private lateinit var tvTitle: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_launcher)

        bindViews()
        setupViews()
    }

    private fun bindViews() {
        vProgress = findViewById(R.id.vProgress)
        tvTitle = findViewById(R.id.tvTitle)
    }

    private fun setupViews() {
        viewModel.apply {
            tvTitle.setText(titleRes)

            onUiShown()

            action.observe(this@PaymentLauncherActivity) {
                it.invoke(this@PaymentLauncherActivity)
            }

            progress.observe(this@PaymentLauncherActivity) {
                vProgress.isVisible = it
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

}