package kz.ioka.android.ioka.presentation.flows.bindCard

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.domain.bindCard.CardBindingResultModel
import kz.ioka.android.ioka.domain.bindCard.CardRepository
import kz.ioka.android.ioka.domain.common.ResultWrapper
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BindCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CardRepository
) : ViewModel() {

    private val launcher = savedStateHandle.get<BindCardLauncher>("BaseActivity_LAUNCHER")

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

    private val _bindRequestState =
        MutableLiveData<BindCardRequestState>(BindCardRequestState.DEFAULT)
    val bindRequestState = _bindRequestState as LiveData<BindCardRequestState>

    init {
        viewModelScope.launch(Dispatchers.Default) {
            allFieldsAreValid.collect { areAllFieldsValid ->
                if (areAllFieldsValid) {
                    _bindRequestState.postValue(BindCardRequestState.DEFAULT)
                } else {
                    _bindRequestState.postValue(BindCardRequestState.DISABLED)
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

    fun onSaveClicked(cardPan: String, expireDate: String, cvv: String) {
        viewModelScope.launch {
            val areAllFieldsValid = allFieldsAreValid.first()

            if (areAllFieldsValid) {
                _bindRequestState.value = BindCardRequestState.LOADING

                val bindCard = repository.bindCard(
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
                        when (bindCard.value) {
                            is CardBindingResultModel.Declined -> _bindRequestState.postValue(
                                BindCardRequestState.ERROR(bindCard.value.cause)
                            )
                            is CardBindingResultModel.Pending -> _bindRequestState.postValue(
                                BindCardRequestState.PENDING(bindCard.value.actionUrl)
                            )
                            else -> _bindRequestState.postValue(BindCardRequestState.SUCCESS)
                        }
                    }
                }
            }
        }
    }

}

sealed class BindCardRequestState {

    object DEFAULT : BindCardRequestState()
    object DISABLED : BindCardRequestState()
    object LOADING : BindCardRequestState()
    object SUCCESS : BindCardRequestState()

    class PENDING(val actionUrl: String) : BindCardRequestState()
    class ERROR(val cause: String? = null) : BindCardRequestState()
}