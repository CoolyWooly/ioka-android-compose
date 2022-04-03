package kz.ioka.android.ioka.presentation.flows.bindCard

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.bindCard.CardBindingResultModel
import kz.ioka.android.ioka.domain.bindCard.CardRepository
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import java.util.*

@Suppress("UNCHECKED_CAST")
internal class BindCardViewModelFactory(
    val launcher: BindCardLauncher,
    private val repository: CardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BindCardViewModel(launcher, repository) as T
    }
}

internal class BindCardViewModel constructor(
    launcher: BindCardLauncher,
    private val repository: CardRepository
) : ViewModel() {

    var cardId: String? = null
    var customerToken: String = launcher.customerToken

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

    fun onBindClicked(cardPan: String, expireDate: String, cvv: String) {
        viewModelScope.launch {
            val areAllFieldsValid = allFieldsAreValid.first()

            if (areAllFieldsValid) {
                _bindRequestState.value = BindCardRequestState.LOADING

                val bindCard = repository.bindCard(
                    customerToken,
                    Config.apiKey,
                    cardPan, expireDate, cvv
                )

                when (bindCard) {
                    is ResultWrapper.Success -> {
                        processSuccessfulResponse(bindCard.value)
                    }
                    is ResultWrapper.IokaError -> {
                        _bindRequestState.postValue(BindCardRequestState.ERROR(bindCard.message))
                    }
                    else -> {
                        _bindRequestState.postValue(BindCardRequestState.ERROR())
                    }
                }
            }
        }
    }

    private fun processSuccessfulResponse(bindCard: CardBindingResultModel) {
        when (bindCard) {
            is CardBindingResultModel.Pending -> {
                cardId = bindCard.cardId
                _bindRequestState.postValue(BindCardRequestState.PENDING(bindCard.actionUrl))
            }
            is CardBindingResultModel.Declined ->
                _bindRequestState.postValue(BindCardRequestState.ERROR(bindCard.cause))
            else ->
                _bindRequestState.postValue(BindCardRequestState.SUCCESS)
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