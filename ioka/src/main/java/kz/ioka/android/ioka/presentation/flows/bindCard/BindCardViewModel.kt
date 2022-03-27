package kz.ioka.android.ioka.presentation.flows.bindCard

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.domain.bindCard.CardBindingResultModel
import kz.ioka.android.ioka.domain.bindCard.CardRepository
import kz.ioka.android.ioka.domain.common.ResultWrapper
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
    private val launcher: BindCardLauncher,
    private val repository: CardRepository
) : ViewModel() {

    private val _isCardPanValid = MutableLiveData(false)
    private val _isExpireDateValid = MutableLiveData(false)
    private val _isCvvValid = MutableLiveData(false)

    private val allFieldsAreValid: MediatorLiveData<Boolean> = MediatorLiveData()

    private val _bindRequestState =
        MutableLiveData<BindCardRequestState>(BindCardRequestState.DEFAULT)
    val bindRequestState = _bindRequestState as LiveData<BindCardRequestState>

    init {
        allFieldsAreValid.addSource(_isCardPanValid) {
            allFieldsAreValid.postValue(allFieldsAreValid.value ?: true && it)
        }
        allFieldsAreValid.addSource(_isExpireDateValid) {
            allFieldsAreValid.postValue(allFieldsAreValid.value ?: true && it)
        }
        allFieldsAreValid.addSource(_isCvvValid) {
            allFieldsAreValid.postValue(allFieldsAreValid.value ?: true && it)
        }

        allFieldsAreValid.observeForever {
            if (it) {
                _bindRequestState.postValue(BindCardRequestState.DEFAULT)
            } else {
                _bindRequestState.postValue(BindCardRequestState.DISABLED)
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
            val areAllFieldsValid = allFieldsAreValid.value ?: false

            if (areAllFieldsValid) {
                _bindRequestState.value = BindCardRequestState.LOADING

                val bindCard = repository.bindCard(
                    launcher.customerToken,
                    launcher.apiKey,
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

    @VisibleForTesting
    fun isCardPanValid(): LiveData<Boolean> {
        return _isCardPanValid
    }

    @VisibleForTesting
    fun isExpireDateValid(): LiveData<Boolean> {
        return _isExpireDateValid
    }

    @VisibleForTesting
    fun isCvvValid(): LiveData<Boolean> {
        return _isCvvValid
    }

    @VisibleForTesting
    fun allFieldsAreValid(): LiveData<Boolean> {
        return allFieldsAreValid
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