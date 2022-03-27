package kz.ioka.android.iokademoapp.presentation.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.toAmountFormat
import kz.ioka.android.iokademoapp.presentation.cart.orderDetail.OrderDetailsActivity
import kz.ioka.android.iokademoapp.presentation.cart.orderDetail.OrderLauncher
import java.math.BigDecimal
import kotlin.properties.Delegates

class CartFragment : Fragment(R.layout.fragment_cart), View.OnClickListener {

    private val initialValue = CartItemDvo(
        id = 248241,
        name = "Набор керамики",
        count = 1,
        price = BigDecimal(148490),
        itemImage = R.drawable.item_image
    )

    private var item: CartItemDvo by Delegates.observable(initialValue) { _, _, newValue ->
        if (context != null)
            setCartItem(newValue)
    }

    private lateinit var tvItemId: AppCompatTextView
    private lateinit var tvItemName: AppCompatTextView
    private lateinit var btnDecrementItemCount: Button
    private lateinit var tvItemCount: AppCompatTextView
    private lateinit var btnIncrementItemCount: Button
    private lateinit var ivItemImage: AppCompatImageView
    private lateinit var tvTotal: AppCompatTextView
    private lateinit var btnContinueOrder: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvItemId = view.findViewById(R.id.tvItemId)
        tvItemName = view.findViewById(R.id.tvItemName)
        btnDecrementItemCount = view.findViewById(R.id.btnDecrementItemCount)
        tvItemCount = view.findViewById(R.id.tvItemCount)
        btnIncrementItemCount = view.findViewById(R.id.btnIncrementItemCount)
        ivItemImage = view.findViewById(R.id.ivItemImage)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnContinueOrder = view.findViewById(R.id.btnContinueOrder)

        item = initialValue

        setupButtons()
    }

    private fun setupButtons() {
        btnIncrementItemCount.setOnClickListener(this)
        btnDecrementItemCount.setOnClickListener(this)
        btnContinueOrder.setOnClickListener(this)
    }

    private fun setCartItem(item: CartItemDvo) {
        tvItemId.text = item.id.toString()
        tvItemName.text = item.name
        tvItemCount.text = item.count.toString()
        ivItemImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), item.itemImage))
        tvTotal.text = item.price.multiply(BigDecimal(item.count)).toAmountFormat()
    }

    override fun onClick(v: View?) {
        when {
            v == btnIncrementItemCount -> {
                val newValue = item.copy(count = item.count + 1)
                item = newValue
            }
            v == btnDecrementItemCount && item.count != 1 -> {
                val newValue = item.copy(count = item.count - 1)
                item = newValue
            }
            v == btnContinueOrder -> {
                val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
                val launcher = OrderLauncher(
                    itemName = item.name,
                    itemImage = item.itemImage,
                    price = item.price.multiply(BigDecimal(item.count))
                )
                intent.putExtra(OrderDetailsActivity.LAUNCHER, launcher)
                startActivity(intent)
            }
        }
    }

}

