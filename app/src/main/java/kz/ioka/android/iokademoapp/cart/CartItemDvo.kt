package kz.ioka.android.iokademoapp.cart

import androidx.annotation.DrawableRes

data class CartItemDvo(
    val id: Int,
    val name: String,
    val count: Int,
    val price: Int,
    @DrawableRes val itemImage: Int
)