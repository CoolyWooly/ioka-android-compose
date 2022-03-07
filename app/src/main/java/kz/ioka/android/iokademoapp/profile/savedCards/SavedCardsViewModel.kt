package kz.ioka.android.iokademoapp.profile.savedCards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.Ioka
import kz.ioka.android.ioka.PaymentFlow
import kz.ioka.android.iokademoapp.BuildConfig
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.ListItem
import kz.ioka.android.iokademoapp.data.CardsRepository
import kz.ioka.android.iokademoapp.data.CustomerRepository
import javax.inject.Inject

@HiltViewModel
class SavedCardsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val _savedCards = MutableLiveData<MutableList<ListItem>>(mutableListOf())
    val savedCards = _savedCards as LiveData<MutableList<ListItem>>

    private val _ioka = MutableLiveData<Ioka>()
    val ioka = _ioka as LiveData<Ioka>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchCards()
        }
    }

    private suspend fun fetchCards() {
        val cardsList = mutableListOf<ListItem>()

        cardsList.addAll(cardsRepository.getSavedCards().map {
            CardDvo(
                id = it.id ?: "",
                cardType = R.drawable.ic_ps_visa,
                cardPan = it.pan_masked ?: "",
            )
        })
        cardsList.add(AddCardDvo() as ListItem)

        _savedCards.postValue(cardsList)
    }

    fun onRemoveCardClicked(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cardsRepository.removeCard(cardId)
            fetchCards()
        }
    }

    fun onAddCardClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val customerToken = customerRepository.getCustomerToken()

            val ioka = Ioka()

            ioka.setup(BuildConfig.API_KEY, PaymentFlow.SaveCardFlow(customerToken))

            _ioka.postValue(ioka)
        }
    }

}