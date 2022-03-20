package kz.ioka.android.ioka.presentation.flows.common

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.domain.cardInfo.CardBrandModel
import kz.ioka.android.ioka.domain.cardInfo.CardEmitterModel
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepository
import kz.ioka.android.ioka.util.Optional
import kz.ioka.android.ioka.util.optional

@Suppress("UNCHECKED_CAST")
class CardInfoViewModelFactory(
    private val cardInfoRepository: CardInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardInfoViewModel(cardInfoRepository) as T
    }
}

class CardInfoViewModel constructor(
    private val cardInfoRepository: CardInfoRepository
) : ViewModel() {

    private val _cardBrand = MutableLiveData<Optional<Int>>()
    val cardBrand = _cardBrand as LiveData<Optional<Int>>

    private val _cardEmitter = MutableLiveData<Optional<Int>>()
    val cardEmitter = _cardEmitter as LiveData<Optional<Int>>

    fun onCardPanEntered(cardPan: String) {
        if (cardPan.matches(Regex("^\\d{1,6}\$"))) {
            getCardBrand(cardPan)
        }

        if (cardPan.matches(Regex("^\\d{6}\$"))) {
            getCardEmitter(cardPan)
        }
    }

    private fun getCardBrand(partialCardPan: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val brand = cardInfoRepository.getBrand(partialCardPan)

            _cardBrand.postValue(
                when (brand) {
                    CardBrandModel.Visa -> R.drawable.ic_ps_visa.optional()
                    CardBrandModel.MasterCard -> R.drawable.ic_ps_mastercard.optional()
                    else -> Optional.empty()
                }
            )
        }
    }

    private fun getCardEmitter(cardPan: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val brand = cardInfoRepository.getEmitter(cardPan)

            _cardEmitter.postValue(
                when (brand) {
                    CardEmitterModel.Alfa -> R.drawable.ic_bank_alfa.optional()
                    CardEmitterModel.Kaspi -> R.drawable.ic_bank_kaspi.optional()
                    else -> Optional.empty()
                }
            )
        }
    }

}