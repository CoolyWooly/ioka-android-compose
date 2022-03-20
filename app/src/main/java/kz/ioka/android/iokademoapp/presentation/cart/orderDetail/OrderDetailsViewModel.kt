package kz.ioka.android.iokademoapp.presentation.cart.orderDetail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.api.PaymentFlow
import kz.ioka.android.iokademoapp.data.OrderRepository
import kz.ioka.android.iokademoapp.presentation.cart.PaymentTypeDvo
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val launcher = savedStateHandle.get<OrderLauncher>("OrderDetailsActivity_LAUNCHER")

    val itemName = launcher?.itemName
    val itemPrice = launcher?.price
    val itemImage = launcher?.itemImage

    private var orderToken: String = ""
    private var customerToken: String = ""

    private val _progress = MutableLiveData(false)
    val progress = _progress as LiveData<Boolean>

    private val _paymentFlow = MutableLiveData<PaymentFlow>()
    val paymentFlow = _paymentFlow as LiveData<PaymentFlow>

    private val _selectedPaymentType = MutableLiveData<PaymentTypeDvo>()
    var selectedPaymentType = _selectedPaymentType as LiveData<PaymentTypeDvo>

    init {
        viewModelScope.launch {
            _progress.postValue(true)

            val checkout = orderRepository.checkout(itemPrice ?: 0)

            orderToken = checkout.orderToken ?: ""
            customerToken = checkout.customerToken ?: ""

            _progress.postValue(false)
        }
    }

    fun onContinueClicked() {
        val paymentFlow = when (selectedPaymentType.value) {
            PaymentTypeDvo.GooglePayDvo -> {
                PaymentFlow.PayWithCardFlow(
                    customerToken,
                    orderToken,
                    itemPrice ?: 0,
                    false
                )
            }
            PaymentTypeDvo.PayWithCardDvo -> {
                PaymentFlow.PayWithCardFlow(
                    customerToken,
                    orderToken,
                    itemPrice ?: 0,
                    false
                )
            }
            PaymentTypeDvo.PayWithCashDvo -> {
                PaymentFlow.PayWithCardFlow(
                    customerToken,
                    orderToken,
                    itemPrice ?: 0,
                    false
                )
            }
            is PaymentTypeDvo.PayWithSavedCardDvo -> {
                PaymentFlow.PayWithCardFlow(
                    customerToken,
                    orderToken,
                    itemPrice ?: 0,
                    false
                )
            }
            null -> {
                PaymentFlow.PayWithCardFlow(
                    customerToken,
                    orderToken,
                    itemPrice ?: 0,
                    true
                )
            }
        }

        _paymentFlow.postValue(paymentFlow)
    }

    fun onPaymentTypeSelected(paymentType: PaymentTypeDvo) {
        _selectedPaymentType.value = paymentType
    }

}