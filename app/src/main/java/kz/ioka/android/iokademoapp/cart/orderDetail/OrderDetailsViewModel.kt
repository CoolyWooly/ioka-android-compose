package kz.ioka.android.iokademoapp.cart.orderDetail

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Ioka
import kz.ioka.android.ioka.PaymentFlow
import kz.ioka.android.iokademoapp.BuildConfig
import kz.ioka.android.iokademoapp.data.OrderRepository
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

    private val _progress = MutableLiveData(false)
    val progress = _progress as LiveData<Boolean>

    private val _ioka = MutableLiveData<Ioka>()
    val ioka = _ioka as LiveData<Ioka>

    fun onContinueClicked() {
        viewModelScope.launch {
            _progress.postValue(true)

            val checkout = orderRepository.checkout(itemPrice ?: 0)

            val ioka = Ioka()
            ioka.setup(
                BuildConfig.API_KEY,
                PaymentFlow.PayWithCardFlow(checkout.customerToken ?: "", checkout.orderToken ?: "")
            )

            _ioka.postValue(ioka)
            _progress.postValue(false)
        }
    }

}