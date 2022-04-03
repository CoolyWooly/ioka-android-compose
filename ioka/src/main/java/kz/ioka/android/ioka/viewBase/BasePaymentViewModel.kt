package kz.ioka.android.ioka.viewBase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kz.ioka.android.ioka.presentation.flows.common.PaymentState

internal abstract class BasePaymentViewModel : ViewModel() {

    private val _payState = MutableLiveData<PaymentState>(PaymentState.DEFAULT)
    val payState = _payState as LiveData<PaymentState>

    fun startPayment() {

    }

}