package kz.ioka.android.iokademoapp.cart.orderDetail

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderLauncher(
    val itemName: String,
    @DrawableRes val itemImage: Int,
    val price: Int
) : Parcelable