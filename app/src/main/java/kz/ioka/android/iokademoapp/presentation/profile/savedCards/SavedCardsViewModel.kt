package kz.ioka.android.iokademoapp.presentation.profile.savedCards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.api.PaymentFlow
import kz.ioka.android.ioka.api.dataSource.IokaDataSource
import kz.ioka.android.ioka.api.dataSource.IokaDataSourceImpl
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.ListItem
import kz.ioka.android.iokademoapp.common.shortPanMask
import kz.ioka.android.iokademoapp.data.CustomerRepository
import kz.ioka.android.iokademoapp.data.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SavedCardsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val settingsRepository: SettingsRepository,
    private val iokaDataSource: IokaDataSource = IokaDataSourceImpl()
) : ViewModel() {

    private val _progress = MutableLiveData(false)
    val progress = _progress as LiveData<Boolean>

    private val _savedCards = MutableLiveData<MutableList<ListItem>>(mutableListOf())
    val savedCards = _savedCards as LiveData<MutableList<ListItem>>

    private val _paymentFlow = MutableLiveData<PaymentFlow>()
    val paymentFlow = _paymentFlow as LiveData<PaymentFlow>

    fun fetchCards() {
        viewModelScope.launch(Dispatchers.IO) {
            _progress.postValue(true)
            val cardsList = mutableListOf<ListItem>()

            settingsRepository.fetchCustomerToken()

            cardsList.addAll(iokaDataSource.getCards(customerRepository.getCustomerToken()).map {
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
            if (iokaDataSource.removeCard(customerToken, cardId))
                fetchCards()
        }
    }

    fun onAddCardClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val customerToken = customerRepository.getCustomerToken()
            val flow = PaymentFlow.BindCardFlow(customerToken)

            _paymentFlow.postValue(flow)
        }
    }

}