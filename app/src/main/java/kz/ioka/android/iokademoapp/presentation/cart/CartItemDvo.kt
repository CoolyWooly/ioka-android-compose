package kz.ioka.android.iokademoapp.presentation.cart

import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class CartItemDvo(
    val id: Int,
    val name: String,
    val count: Int,
    val price: BigDecimal,
    @DrawableRes val itemImage: Int
)