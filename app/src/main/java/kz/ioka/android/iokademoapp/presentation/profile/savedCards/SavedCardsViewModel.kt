package kz.ioka.android.iokademoapp.presentation.profile.savedCards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.api.Ioka
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.ListItem
import kz.ioka.android.iokademoapp.common.shortPanMask
import kz.ioka.android.iokademoapp.data.CustomerRepository
import kz.ioka.android.iokademoapp.data.SettingsRepository
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SavedCardsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _progress = MutableLiveData(false)
    val progress = _progress as LiveData<Boolean>

    private val _savedCards = MutableLiveData<MutableList<ListItem>>(mutableListOf())
    val savedCards = _savedCards as LiveData<MutableList<ListItem>>

    lateinit var customerToken: String

    fun fetchCards() {
        viewModelScope.launch(Dispatchers.IO) {
            _progress.postValue(true)
            val cardsList = mutableListOf<ListItem>()

            settingsRepository.fetchCustomerToken()
            customerToken = customerRepository.getCustomerToken()

            cardsList.addAll(Ioka.getCards(customerToken).map {
                CardDvo(
                    id = it.id ?: "",
                    cardType = R.drawable.ic_ps_visa,
                    cardPan = it.panMasked?.shortPanMask() ?: "",
                )
            })
            cardsList.add(AddCardDvo() as ListItem)

            _savedCards.postValue(cardsList)
            _progress.postValue(false)
        }
    }

    fun onRemoveCardClicked(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val customerToken = customerRepository.getCustomerToken()

            try {
                val isCardRemoved = Ioka.removeCard(customerToken, cardId)

                if (isCardRemoved) fetchCards()
            } catch (e: Exception) {
                Log.d("Error", "" + e.localizedMessage)
            }
        }
    }

}