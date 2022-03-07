package kz.ioka.android.ioka.flows.bindCard

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.domain.bindCard.CardBindingResultModel
import kz.ioka.android.ioka.domain.bindCard.CardRepository
import kz.ioka.android.ioka.domain.common.ResultWrapper
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SaveCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CardRepository
) : ViewModel() {

    private val launcher = savedStateHandle.get<BindCardLauncher>("BaseActivity_LAUNCHER")

    private val _cardPanError = MutableLiveData(false)
    val cardPanError = _cardPanError as LiveData<Boolean>

    private val _expireDateError = MutableLiveData(false)
    val expireDateError = _expireDateError as LiveData<Boolean>

    private val _cvvError = MutableLiveData(false)
    val cvvError = _cvvError as LiveData<Boolean>

    private var isCardPanValid = false
    private var isExpireDateValid = false
    private var isCvvValid = false

    private var needToValidate = MutableStateFlow(false)

    private val _bindRequestState =
        MutableLiveData<BindCardRequestState>(BindCardRequestState.DEFAULT)
    val bindRequestState = _bindRequestState as LiveData<BindCardRequestState>

    init {
        viewModelScope.launch(Dispatchers.Default) {
            needToValidate.collect {
                if (it) {
                    _cardPanError.postValue(!isCardPanValid)
                    _expireDateError.postValue(!isExpireDateValid)
                    _cvvError.postValue(!isCvvValid)
                }
            }
        }
    }

    fun onCardPanEntered(cardPan: String) {
        isCardPanValid = cardPan.length in 15..19

        if (needToValidate.value) {
            _cardPanError.value = !isCardPanValid
        }
    }

    fun onExpireDateEntered(expireDate: String) {
        isExpireDateValid = if (expireDate.length < 4) {
            false
        } else {
            val month = expireDate.substring(0..1).toInt()
            val year = expireDate.substring(2).toInt()

            val currentTime = Calendar.getInstance()
            val currentMonth = currentTime.get(Calendar.MONTH)
            val currentYear = currentTime.get(Calendar.YEAR) - 2000

            month <= 12 && (year > currentYear || (year == currentYear && month >= currentMonth))
        }

        if (needToValidate.value) {
            _expireDateError.value = !isExpireDateValid
        }
    }

    fun onCvvEntered(cvv: String) {
        isCvvValid = cvv.length == 3

        if (needToValidate.value) {
            _cvvError.value = !isCvvValid
        }
    }

    fun onSaveClicked(cardPan: String, expireDate: String, cvv: String) {
        if (isCvvValid && isExpireDateValid && isCardPanValid) {
            _bindRequestState.value = BindCardRequestState.LOADING

            viewModelScope.launch {
                val bindCard = repository.saveCard(
                    launcher?.customerToken ?: "",
                    launcher?.apiKey ?: "",
                    cardPan, expireDate, cvv
                )

                when (bindCard) {
                    is ResultWrapper.GenericError -> {
                        _bindRequestState.postValue(BindCardRequestState.ERROR())
                    }
                    is ResultWrapper.NetworkError -> {
                        _bindRequestState.postValue(BindCardRequestState.ERROR())
                    }
                    is ResultWrapper.Success -> {
                        if (bindCard.value is CardBindingResultModel.Declined)
                            _bindRequestState.postValue(BindCardRequestState.ERROR(bindCard.value.cause))
                        else
                            _bindRequestState.postValue(BindCardRequestState.SUCCESS)
                    }
                }
            }
        } else {
            needToValidate.value = true
        }
    }

}

sealed class BindCardRequestState {

    object DEFAULT : BindCardRequestState()
    object LOADING : BindCardRequestState()
    object SUCCESS : BindCardRequestState()
    class ERROR(val cause: String? = null) : BindCardRequestState()
}