package kz.ioka.android.ioka.uikit

import kz.ioka.android.ioka.util.Validator

class CvvValidator : Validator {

    override fun validate(input: String): Boolean {
        return input.length in 3..4
    }

}