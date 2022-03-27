package kz.ioka.android.iokademoapp.presentation.cart.orderDetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.ioka.api.Ioka
import kz.ioka.android.iokademoapp.BaseActivity
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.toAmountFormat
import kz.ioka.android.iokademoapp.presentation.cart.PaymentTypeDvo
import kz.ioka.android.iokademoapp.presentation.cart.paymentType.SelectPaymentTypeActivity
import kz.ioka.android.iokademoapp.presentation.cart.paymentType.SelectedPaymentTypeView
import java.math.BigDecimal

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
    private lateinit var vPaymentType: SelectedPaymentTypeView
    private lateinit var btnContinueOrder: Button
    private lateinit var vProgress: FrameLayout

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.getParcelableExtra<PaymentTypeDvo>(SelectPaymentTypeActivity.LAUNCHER)
                    ?.let { paymentType ->
                        viewModel.onPaymentTypeSelected(paymentType)
                    }
            }
        }

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
        vPaymentType = findViewById(R.id.vPaymentType)
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
            tvPrice.text = (itemPrice ?: BigDecimal.ZERO).toAmountFormat()

            progress.observe(this@OrderDetailsActivity) {
                vProgress.isVisible = it
            }

            selectedPaymentType.observe(this@OrderDetailsActivity) {
                vPaymentType.setPaymentType(it)
            }

            paymentFlow.observe(this@OrderDetailsActivity) {
                Ioka.showForm(it).invoke(this@OrderDetailsActivity)
            }
        }
    }

    private fun setupListeners() {
        vPaymentType.setOnClickListener(this)
        btnContinueOrder.setOnClickListener(this)
        vToolbar.setNavigationOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnContinueOrder -> {
                viewModel.onContinueClicked()
            }
            vPaymentType -> {
                val intent = Intent(this, SelectPaymentTypeActivity::class.java)

                viewModel.selectedPaymentType.value?.let {
                    intent.putExtra(SelectPaymentTypeActivity.LAUNCHER, it)
                }

                startForResult.launch(Intent(this, SelectPaymentTypeActivity::class.java))
            }
            else -> {
                onBackPressed()
            }
        }
    }
}