package kz.ioka.android.ioka.presentation.flows.payWithCard

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.common.ResultWrapper
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.util.getOrderId
import java.util.*

@Suppress("UNCHECKED_CAST")
internal class PayWithCardViewModelFactory(
    val launcher: PayWithCardLauncher,
    private val paymentRepository: PaymentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PayWithCardViewModel(launcher, paymentRepository) as T
    }
}

internal class PayWithCardViewModel constructor(
    val launcher: PayWithCardLauncher,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    val price = launcher.price
    private var paymentId: String = ""

    private val _isCardPanValid = MutableStateFlow(false)
    private val _isExpireDateValid = MutableStateFlow(false)
    private val _isCvvValid = MutableStateFlow(false)

    private val allFieldsAreValid: Flow<Boolean> = combine(
        _isCardPanValid,
        _isExpireDateValid,
        _isCvvValid
    ) { isCardPanValid, isExpireDateValid, isCvvValid ->
        isCardPanValid && isExpireDateValid && isCvvValid
    }

    private val _payState = MutableLiveData<PayState>(PayState.DEFAULT)
    val payState = _payState as LiveData<PayState>

    init {
        viewModelScope.launch {
            allFieldsAreValid.collect { areAllFieldsValid ->
                if (areAllFieldsValid) {
                    _payState.postValue(PayState.DEFAULT)
                } else {
                    _payState.postValue(PayState.DISABLED)
                }
            }
        }
    }

    fun onCardPanEntered(cardPan: String) {
        _isCardPanValid.value = cardPan.length in 15..19
    }

    fun onExpireDateEntered(expireDate: String) {
        _isExpireDateValid.value = if (expireDate.length < 4) {
            false
        } else {
            val month = expireDate.substring(0..1).toInt()
            val year = expireDate.substring(2).toInt()

            val currentTime = Calendar.getInstance()
            val currentMonth = currentTime.get(Calendar.MONTH)
            val currentYear = currentTime.get(Calendar.YEAR) - 2000

            month <= 12 && (year > currentYear || (year == currentYear && month >= currentMonth))
        }
    }

    fun onCvvEntered(cvv: String) {
        _isCvvValid.value = cvv.length == 3
    }

    fun onPayClicked(cardPan: String, expireDate: String, cvv: String, bindCard: Boolean) {
        viewModelScope.launch {
            val areAllFieldsValid = allFieldsAreValid.first()

            if (areAllFieldsValid) {
                _payState.postValue(PayState.LOADING)

                val cardPayment = paymentRepository.createCardPayment(
                    launcher.orderToken.getOrderId(),
                    launcher.customerToken,
                    Config.apiKey,
                    cardPan, expireDate, cvv, bindCard
                )

                when (cardPayment) {
                    is ResultWrapper.Success -> {
                        processSuccessfulResponse(cardPayment.value)
                    }
                    is ResultWrapper.IokaError -> {
                        _payState.postValue(PayState.FAILED(cardPayment.message))
                    }
                    else -> {
                        _payState.postValue(PayState.ERROR())
                    }
                }
            }
        }
    }

    private fun processSuccessfulResponse(cardPayment: PaymentModel) {
        when (cardPayment) {
            is PaymentModel.Pending -> {
                paymentId = cardPayment.paymentId
                _payState.postValue(PayState.PENDING(cardPayment.actionUrl))
            }
            is PaymentModel.Declined -> _payState.postValue(PayState.FAILED(cardPayment.message))
            else -> _payState.postValue(PayState.SUCCESS)
        }
    }

    fun on3DSecurePassed() {
        viewModelScope.launch {
            _payState.postValue(PayState.LOADING)

            val cardPayment = paymentRepository.isPaymentSuccessful(
                Config.apiKey,
                launcher.customerToken,
                launcher.orderToken,
                paymentId
            )

            when (cardPayment) {
                is ResultWrapper.Success -> {
                    processSuccessfulResponse(cardPayment.value)
                }
                is ResultWrapper.IokaError -> {
                    _payState.postValue(PayState.FAILED())
                }
                else -> {
                    _payState.postValue(PayState.ERROR())
                }
            }
        }
    }

}

sealed class PayState {

    object DEFAULT : PayState()
    object DISABLED : PayState()
    object LOADING : PayState()
    object SUCCESS : PayState()
    class FAILED(val cause: String? = null) : PayState()
    class ERROR(val cause: String? = null) : PayState()

    class PENDING(val actionUrl: String) : PayState()
}