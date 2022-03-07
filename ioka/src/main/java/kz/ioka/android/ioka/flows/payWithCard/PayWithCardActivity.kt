package kz.ioka.android.ioka.flows.payWithCard

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.viewBase.BaseActivity

@AndroidEntryPoint
class PayWithCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_with_card)
    }

}