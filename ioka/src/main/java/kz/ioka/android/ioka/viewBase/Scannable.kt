package kz.ioka.android.ioka.viewBase

import android.content.Intent
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard

interface Scanable {

    fun onCardScanned(cardNumber: String)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BaseActivity.SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                val scanResult: CreditCard? =
                    data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)

                onCardScanned(scanResult?.formattedCardNumber ?: "")
            }
        }
    }

}