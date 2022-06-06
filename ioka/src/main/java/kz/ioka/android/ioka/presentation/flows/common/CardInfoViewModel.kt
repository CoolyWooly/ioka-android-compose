package kz.ioka.android.ioka.presentation.flows.common

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.cardInfo.CardBrandModel
import kz.ioka.android.ioka.domain.cardInfo.CardEmitterModel
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepository
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepositoryImpl
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.util.Optional

internal class CardInfoViewModel constructor(
    private var cardInfoRepository: CardInfoRepository =
        CardInfoRepositoryImpl(DependencyInjector.cardInfoApi)
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
        viewModelScope.launch {
            val brandResponse = cardInfoRepository.getBrand(partialCardPan)

            if (brandResponse is ResultWrapper.Success) {
                _cardBrand.postValue(Optional.of(CardBrandDvo(brandResponse.value.iconRes)))
            } else {
                _cardBrand.postValue(Optional.of(CardBrandDvo(CardBrandModel.Unknown.iconRes)))
            }
        }
    }

    private fun getCardEmitter(cardPan: String) {
        viewModelScope.launch {
            val emitterResponse = cardInfoRepository.getEmitter(cardPan)

            if (emitterResponse is ResultWrapper.Success) {
                _cardEmitter.postValue(Optional.of(CardEmitterDvo(emitterResponse.value.iconRes)))
            } else {
                _cardEmitter.postValue(Optional.of(CardEmitterDvo(CardEmitterModel.Unknown.iconRes)))
            }
        }
    }

}