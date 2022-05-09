@file:Suppress("KDocUnresolvedReference")

package kz.ioka.android.ioka.viewBase

import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import kz.ioka.android.ioka.SCAN_REQUEST_CODE

/**
 * Интерфейс для вьюшек (фрагментов, активити), которые вызывают сканер карты
 *
 * Используется библиотека:
 * https://github.com/card-io/card.io-Android-SDK
 */
internal interface Scannable {

    /**
     * Абстрактный метод, в котором надо реализовать обработку отсканированного номер карты
     * @param cardNumber
     *              номер отсканированной карты
     */
    fun onCardScanned(cardNumber: String)

    /**
     * Начинает активность сканера карты
     * @param context
     *              текущий контекст, из которого вызывается метод
     */
    fun startCardScanner(context: Context) {
        val scanIntent = Intent(context, CardIOActivity::class.java)

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false)
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true)

        ActivityCompat.startActivityForResult(
            context as BaseActivity,
            scanIntent,
            SCAN_REQUEST_CODE,
            null
        )
    }

    /**
     * Перехватывает результат активности сканера, надо вызвать в одноименном методе активности
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (
            requestCode == SCAN_REQUEST_CODE &&
            data != null &&
            data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)
        ) {
            val scanResult: CreditCard? =
                data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)

            onCardScanned(scanResult?.formattedCardNumber ?: "")
        }
    }

}