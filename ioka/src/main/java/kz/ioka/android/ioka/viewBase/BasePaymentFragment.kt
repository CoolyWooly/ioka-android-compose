package kz.ioka.android.ioka.viewBase

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import kz.ioka.android.ioka.presentation.webView.PaymentConfirmationBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewActivity

internal abstract class BasePaymentFragment(@LayoutRes private val layoutRes: Int) :
    DialogFragment(layoutRes) {

    abstract fun onSuccessfulPayment()

    abstract fun onFailedPayment(cause: String? = null)

    fun onActionRequired(
        actionUrl: String,
        orderToken: String,
        paymentId: String
    ) {
        val intent = WebViewActivity.provideIntent(
            requireContext(), PaymentConfirmationBehavior(
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