package kz.ioka.android.ioka.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.data.payment.PaymentApi
import kz.ioka.android.ioka.domain.bindCard.CardRepository
import kz.ioka.android.ioka.domain.bindCard.CardRepositoryImpl
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepository
import kz.ioka.android.ioka.domain.cardInfo.CardInfoRepositoryImpl
import kz.ioka.android.ioka.domain.payment.PaymentRepository
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun provideCardInfoRepository(cardInfoApi: CardInfoApi): CardInfoRepository {
        return CardInfoRepositoryImpl(cardInfoApi)
    }

    @Provides
    fun provideCardRepository(cardApi: CardApi): CardRepository {
        return CardRepositoryImpl(cardApi)
    }

    @Provides
    fun providePaymentRepository(paymentApi: PaymentApi): PaymentRepository {
        return PaymentRepositoryImpl(paymentApi)
    }

}