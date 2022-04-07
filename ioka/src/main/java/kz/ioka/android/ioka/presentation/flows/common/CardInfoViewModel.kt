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
internal class CardInfoViewModelFactory(
    private val cardInfoRepository: CardInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CardInfoViewModel(cardInfoRepository) as T
    }
}

internal class CardInfoViewModel constructor(
    private val cardInfoRepository: CardInfoRepository
) : ViewModel() {

    companion object {
        const val REGEX_BRAND_FETCHABLE = "^\\d{1,6}\$"
        const val REGEX_EMITTER_FETCHABLE = "^\\d{6}\$"
    }

    private val _cardBrand = MutableLiveData<Optional<CardBrandDvo>>()
    val cardBrand = _cardBrand as LiveData<Optional<CardBrandDvo>>

    private val _cardEmitter = MutableLiveData<Optional<CardEmitterDvo>>()
    val cardEmitter = _cardEmitter as LiveData<Optional<CardEmitterDvo>>

    fun onCardPanEntered(cardPan: String) {
        if (
            cardPan.matches(Regex(REGEX_BRAND_FETCHABLE)) &&
            _cardBrand.value?.isNotPresent() == true
        ) {
            getCardBrand(cardPan)
        } else if (cardPan.isEmpty()) {
            _cardBrand.value = Optional.empty()
        }

        if (
            cardPan.matches(Regex(REGEX_EMITTER_FETCHABLE)) &&
            _cardEmitter.value?.isNotPresent() == true
        ) {
            getCardEmitter(cardPan)
        } else if (cardPan.length < 6) {
            _cardEmitter.value = Optional.empty()
        }
    }

    private fun getCardBrand(partialCardPan: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val brand = cardInfoRepository.getBrand(partialCardPan)

            _cardBrand.postValue(Optional.of(CardBrandDvo(brand.iconRes)))
        }
    }

    private fun getCardEmitter(cardPan: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val emitter = cardInfoRepository.getEmitter(cardPan)

            _cardEmitter.postValue(Optional.of(CardEmitterDvo(emitter.iconRes)))
        }
    }

}