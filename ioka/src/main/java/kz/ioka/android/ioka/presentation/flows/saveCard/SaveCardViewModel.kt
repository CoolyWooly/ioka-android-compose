package kz.ioka.android.ioka.presentation.flows.saveCard

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.saveCard.CardRepository
import kz.ioka.android.ioka.domain.saveCard.CardRepositoryImpl
import kz.ioka.android.ioka.domain.saveCard.SaveCardResultModel

@Suppress("UNCHECKED_CAST")
internal class SaveCardViewModelFactory(
    val launcher: SaveCardLauncher
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SaveCardViewModel(
            launcher,
            CardRepositoryImpl(DependencyInjector.cardApi)
        ) as T
    }
}

internal class SaveCardViewModel constructor(
    launcher: SaveCardLauncher,
    private val repository: CardRepository
) : ViewModel() {

    var cardId: String? = null
    var customerToken: String = launcher.customerToken

    private var isCardNumberValid = false
    private var isExpiryDateValid = false
    private var isCvvValid = false

    private val _isPayAvailable = MutableLiveData(false)
    val isPayAvailable = _isPayAvailable as LiveData<Boolean>

    private val _saveRequestState =
        MutableLiveData<SaveCardRequestState>(SaveCardRequestState.DEFAULT)
    val saveRequestState = _saveRequestState as LiveData<SaveCardRequestState>

    fun onSaveClicked(cardPan: String, expireDate: String, cvv: String) {
        viewModelScope.launch {
            val areAllFieldsValid = _isPayAvailable.value

            if (areAllFieldsValid == true) {
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

    fun setIsCardNumberValid(isValid: Boolean) {
        isCardNumberValid = isValid

        _isPayAvailable.value = isCardNumberValid && isExpiryDateValid && isCvvValid
    }

    fun setIsExpiryDateValid(isValid: Boolean) {
        isExpiryDateValid = isValid

        _isPayAvailable.value = isCardNumberValid && isExpiryDateValid && isCvvValid
    }

    fun setIsCvvValid(isValid: Boolean) {
        isCvvValid = isValid

        _isPayAvailable.value = isCardNumberValid && isExpiryDateValid && isCvvValid
    }

}

internal sealed class SaveCardRequestState {

    object DEFAULT : SaveCardRequestState()
    object LOADING : SaveCardRequestState()
    object SUCCESS : SaveCardRequestState()

    class PENDING(val actionUrl: String) : SaveCardRequestState()
    class ERROR(val cause: String? = null) : SaveCardRequestState()
}