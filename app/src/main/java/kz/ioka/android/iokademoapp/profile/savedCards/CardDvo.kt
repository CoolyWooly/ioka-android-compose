package kz.ioka.android.iokademoapp.profile.savedCards

import androidx.annotation.DrawableRes
import kz.ioka.android.iokademoapp.common.ListItem

data class CardDvo(
    val id: String,
    @DrawableRes val cardType: Int,
    val cardPan: String,
) : ListItem

class AddCardDvo : ListItem