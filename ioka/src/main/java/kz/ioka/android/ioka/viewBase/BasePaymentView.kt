package kz.ioka.android.ioka.viewBase

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity

internal interface BasePaymentView {

    fun provideContext(): Context

    fun registerForActivityResult(
        contract: ActivityResultContracts.StartActivityForResult,
        callback: ActivityResultCallback<ActivityResult>
    ): ActivityResultLauncher<Intent>

    fun onSuccessfulPayment()

    fun onFailedPayment(cause: String? = null)

    fun onActionRequired(
        actionUrl: String,
        orderToken: String,
        paymentId: String
    ) {
        val intent = WebViewActivity.provideIntent(
            provideContext(), PaymentConfirmationBehavior(
                url = actionUrl,
                orderToken = orderToken,
                paymentId = paymentId
            )
        )

        startForResult().launch(intent)
    }

    fun startForResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                onSuccessfulPayment()
            } else if (it.resultCode == AppCompatActivity.RESULT_CANCELED) {
                onFailedPayment()
            }
        }
    }

}