package kz.ioka.android.ioka.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class ApiModule {

    @Provides
    fun provideCardInfoApi(retrofit: Retrofit): CardInfoApi {
        return retrofit.create(CardInfoApi::class.java)
    }

    @Provides
    fun provideCardApi(retrofit: Retrofit): CardApi {
        return retrofit.create(CardApi::class.java)
    }

}