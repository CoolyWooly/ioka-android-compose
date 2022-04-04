package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.order.OrderRepository
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.presentation.flows.common.OrderDvo
import kz.ioka.android.ioka.presentation.flows.common.PaymentState
import kz.ioka.android.ioka.util.getOrderId

internal class PayWithCardIdViewModelFactory(
    private val launcher: PayWithCardIdLauncher,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PayWithCardIdViewModel(launcher, orderRepository, paymentRepository) as T
    }

}

internal class PayWithCardIdViewModel(
    launcher: PayWithCardIdLauncher,
    orderRepository: OrderRepository,
    paymentRepository: PaymentRepository,
) : ViewModel() {

    var orderToken: String = launcher.orderToken
    lateinit var order: OrderDvo
    lateinit var paymentId: String

    private val _payState = MutableLiveData<PaymentState>(PaymentState.DEFAULT)
    val payState = _payState as LiveData<PaymentState>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val orderResponse = orderRepository.getOrderById(orderToken.getOrderId())

            if (orderResponse is ResultWrapper.Success) {
                order = OrderDvo(orderResponse.value.externalId, orderResponse.value.amount)

                val paymentResponse = paymentRepository.createPaymentWithCardId(
                    orderToken.getOrderId(),
                    Config.apiKey,
                    launcher.cardId
                )

                when (paymentResponse) {
                    is ResultWrapper.Success -> {
                        processSuccessfulResponse(paymentResponse.value)
                    }
                    is ResultWrapper.IokaError -> {
                        _payState.postValue(PaymentState.FAILED(paymentResponse.message))
                    }
                    else -> {
                        _payState.postValue(PaymentState.ERROR())
                    }
                }
            } else {
                _payState.postValue(PaymentState.ERROR())
            }
        }
    }

    private fun processSuccessfulResponse(cardPayment: PaymentModel) {
        when (cardPayment) {
            is PaymentModel.Pending -> {
                paymentId = cardPayment.paymentId
                _payState.postValue(PaymentState.PENDING(cardPayment.actionUrl))
            }
            is PaymentModel.Declined -> _payState.postValue(PaymentState.FAILED(cardPayment.message))
            else -> _payState.postValue(PaymentState.SUCCESS)
        }
    }

}