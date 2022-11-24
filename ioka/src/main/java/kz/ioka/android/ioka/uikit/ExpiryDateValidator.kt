package kz.ioka.android.ioka.uikit

import kz.ioka.android.ioka.util.Validator
import java.util.*

class ExpiryDateValidator : Validator {

    override fun validate(input: String): Boolean {
        return if (input.length < 4) {
            false
        } else {
            val month = input.substring(0..1).toInt()
            val year = input.substring(2).toInt()

            val currentTime = Calendar.getInstance()
            val currentMonth = currentTime.get(Calendar.MONTH)
            val currentYear = currentTime.get(Calendar.YEAR) - 2000

            month <= 12 && (year > currentYear || (year == currentYear && month >= currentMonth))
        }
    }

}