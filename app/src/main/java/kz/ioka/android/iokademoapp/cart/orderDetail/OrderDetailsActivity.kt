package kz.ioka.android.iokademoapp.cart.orderDetail

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.iokademoapp.BaseActivity
import kz.ioka.android.iokademoapp.R

@AndroidEntryPoint
class OrderDetailsActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val LAUNCHER = "OrderDetailsActivity_LAUNCHER"
    }

    private val viewModel: OrderDetailsViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar
    private lateinit var tvItemName: TextView
    private lateinit var ivItemImage: ImageView
    private lateinit var tvPrice: TextView
    private lateinit var btnContinueOrder: Button
    private lateinit var vProgress: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        bindViews()
        observeData()
        setupListeners()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        tvItemName = findViewById(R.id.tvItemName)
        ivItemImage = findViewById(R.id.ivItemImage)
        tvPrice = findViewById(R.id.tvPrice)
        btnContinueOrder = findViewById(R.id.btnContinueOrder)
        vProgress = findViewById(R.id.vProgress)
    }

    private fun observeData() {
        viewModel.run {
            tvItemName.text = itemName
            ivItemImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this@OrderDetailsActivity,
                    itemImage ?: R.drawable.ic_cart
                )
            )
            tvPrice.text = itemPrice.toString()

            progress.observe(this@OrderDetailsActivity) {
                vProgress.isVisible = it
            }

            ioka.observe(this@OrderDetailsActivity) {
                it.showForm().invoke(this@OrderDetailsActivity)
            }
        }
    }

    private fun setupListeners() {
        btnContinueOrder.setOnClickListener(this)
        vToolbar.setNavigationOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == btnContinueOrder) {
            viewModel.onContinueClicked()
        } else {
            onBackPressed()
        }
    }
}