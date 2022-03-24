package kz.ioka.android.ioka.viewBase

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard

internal abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val LAUNCHER = "BaseActivity_LAUNCHER"
        const val SCAN_REQUEST_CODE = 420
    }

    fun <T : Parcelable> launcher(): T? {
        return intent.getParcelableExtra(LAUNCHER)
    }

    open fun onCardScanned(cardNumber: String) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                val scanResult: CreditCard? =
                    data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)

                onCardScanned(scanResult?.formattedCardNumber ?: "")
            }
        }
    }

}