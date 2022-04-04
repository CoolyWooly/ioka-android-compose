package kz.ioka.android.ioka.viewBase

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity

internal abstract class BasePaymentActivity : BaseActivity() {

    abstract fun onSuccessfulPayment()

    abstract fun onFailedPayment(cause: String? = null)

    fun onActionRequired(
        actionUrl: String,
        orderToken: String,
        paymentId: String
    ) {
        val intent = WebViewActivity.provideIntent(
            this, PaymentConfirmationBehavior(
                url = actionUrl,
                orderToken = orderToken,
                paymentId = paymentId
            )
        )

        startForResult.launch(intent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                onSuccessfulPayment()
            } else if (it.resultCode == RESULT_CANCELED) {
                onFailedPayment()
            }
        }

}