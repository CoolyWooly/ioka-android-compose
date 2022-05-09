package kz.ioka.android.ioka.presentation.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.IOKA_EXTRA_RESULT_NAME
import kz.ioka.android.ioka.util.getDrawableFromRes
import kz.ioka.android.ioka.util.toAmountFormat
import kz.ioka.android.ioka.viewBase.BaseActivity
import java.math.BigDecimal

internal class ResultActivity : BaseActivity() {

    companion object {
        fun provideIntent(context: Context, launcher: ResultLauncher): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(LAUNCHER, launcher)
            }
        }
    }

    private lateinit var launcher: ResultLauncher

    lateinit var vToolbar: Toolbar
    lateinit var ivStatus: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var tvSubtitle: AppCompatTextView
    lateinit var tvAmount: AppCompatTextView
    lateinit var btnAction: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ioka_activity_result)

        launcher = launcher()!!
        bindViews()
        setData()
        setupListeners()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        ivStatus = findViewById(R.id.ivStatus)
        tvTitle = findViewById(R.id.tvTitle)
        tvSubtitle = findViewById(R.id.tvSubtitle)
        tvAmount = findViewById(R.id.tvAmount)
        btnAction = findViewById(R.id.btnAction)
    }

    private fun setData() {
        launcher.let {
            ivStatus.setImageDrawable(getDrawableFromRes(it.statusIconRes))
            tvTitle.setText(it.titleRes)
            tvTitle.setTextColor(ContextCompat.getColor(this, it.titleColorRes))
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
            finishWithResult()
        }
    }

    private fun finishWithResult() {
        setResult(RESULT_OK, Intent().apply {
            putExtra(IOKA_EXTRA_RESULT_NAME, launcher.flowResult)
        })
        finish()
    }

}