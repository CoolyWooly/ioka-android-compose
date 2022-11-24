package kz.ioka.android.ioka.uikit

import kz.ioka.android.ioka.util.Validator

class CardNumberValidator : Validator {

    companion object {
        private val DEFAULT_CARD_NUMBER_LENGTH_RANGE = 15..19
    }

    private var cardNumberLengthRange = DEFAULT_CARD_NUMBER_LENGTH_RANGE

    override fun validate(input: String): Boolean {
        return input.length in cardNumberLengthRange
    }

    fun setCardNumberLengthRange(cardNumberLengthRange: IntRange) {
        this.cardNumberLengthRange = cardNumberLengthRange
    }

}