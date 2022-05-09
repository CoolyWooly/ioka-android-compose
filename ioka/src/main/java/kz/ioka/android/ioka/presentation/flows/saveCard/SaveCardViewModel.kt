package kz.ioka.android.ioka.presentation.flows.saveCard

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.domain.saveCard.SaveCardResultModel
import kz.ioka.android.ioka.domain.saveCard.CardRepository
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import java.util.*

@Suppress("UNCHECKED_CAST")
internal class SaveCardViewModelFactory(
    val launcher: SaveCardLauncher,
    private val repository: CardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SaveCardViewModel(launcher, repository) as T
    }
}

internal class SaveCardViewModel constructor(
    launcher: SaveCardLauncher,
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

    private val _saveRequestState =
        MutableLiveData<SaveCardRequestState>(SaveCardRequestState.DEFAULT)
    val saveRequestState = _saveRequestState as LiveData<SaveCardRequestState>

    init {
        viewModelScope.launch(Dispatchers.Default) {
            allFieldsAreValid.collect { areAllFieldsValid ->
                if (areAllFieldsValid) {
                    _saveRequestState.postValue(SaveCardRequestState.DEFAULT)
                } else {
                    _saveRequestState.postValue(SaveCardRequestState.DISABLED)
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
        _isCvvValid.value = cvv.length in 3..4
    }

    fun onSaveClicked(cardPan: String, expireDate: String, cvv: String) {
        viewModelScope.launch {
            val areAllFieldsValid = allFieldsAreValid.first()

            if (areAllFieldsValid) {
                _saveRequestState.value = SaveCardRequestState.LOADING

                val saveCard = repository.saveCard(
                    customerToken,
                    Config.apiKey,
                    cardPan, expireDate, cvv
                )

                when (saveCard) {
                    is ResultWrapper.Success -> {
                        processSuccessfulResponse(saveCard.value)
                    }
                    is ResultWrapper.IokaError -> {
                        _saveRequestState.postValue(SaveCardRequestState.ERROR(saveCard.message))
                    }
                    else -> {
                        _saveRequestState.postValue(SaveCardRequestState.ERROR())
                    }
                }
            }
        }
    }

    private fun processSuccessfulResponse(saveCard: SaveCardResultModel) {
        when (saveCard) {
            is SaveCardResultModel.Pending -> {
                cardId = saveCard.cardId
                _saveRequestState.postValue(SaveCardRequestState.PENDING(saveCard.actionUrl))
            }
            is SaveCardResultModel.Declined ->
                _saveRequestState.postValue(SaveCardRequestState.ERROR(saveCard.cause))
            else ->
                _saveRequestState.postValue(SaveCardRequestState.SUCCESS)
        }
    }

}

internal sealed class SaveCardRequestState {

    object DEFAULT : SaveCardRequestState()
    object DISABLED : SaveCardRequestState()
    object LOADING : SaveCardRequestState()
    object SUCCESS : SaveCardRequestState()

    class PENDING(val actionUrl: String) : SaveCardRequestState()
    class ERROR(val cause: String? = null) : SaveCardRequestState()
}