package kz.ioka.android.ioka.viewBase

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kz.ioka.android.ioka.api.FlowResult
import kz.ioka.android.ioka.api.IOKA_EXTRA_RESULT_NAME

internal abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val LAUNCHER = "BaseActivity_LAUNCHER"
    }

    fun <T : Parcelable> launcher(): T? {
        return intent.getParcelableExtra(LAUNCHER)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1)
            finishWithCanceledResult()
        else
            super.onBackPressed()
    }

    fun finishWithCanceledResult() {
        finishWithResult(RESULT_CANCELED, FlowResult.Cancelled)
    }

    fun finishWithSucceededResult() {
        finishWithResult(RESULT_OK, FlowResult.Succeeded)
    }

    fun finishWithFailedResult(cause: String) {
        finishWithResult(RESULT_OK, FlowResult.Failed(cause))
    }

    fun finishWithResult(resultCode: Int, flowResult: FlowResult) {
        setResult(resultCode, Intent().apply {
            putExtra(IOKA_EXTRA_RESULT_NAME, flowResult)
        })

        finish()
    }

}