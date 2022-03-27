package kz.ioka.android.iokademoapp.presentation.cart.orderDetail

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class OrderLauncher(
    val itemName: String,
    @DrawableRes val itemImage: Int,
    val price: BigDecimal
) : Parcelable