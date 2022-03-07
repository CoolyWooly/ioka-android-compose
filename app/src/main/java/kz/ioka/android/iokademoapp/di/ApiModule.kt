package kz.ioka.android.iokademoapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.ioka.android.iokademoapp.data.remote.CardsApi
import kz.ioka.android.iokademoapp.data.remote.DemoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    fun provideGson(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Named("IokaRetrofit")
    fun provideRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://stage-api.ioka.kz")
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Named("DemoRetrofit")
    fun provideDemoRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ioka-example-mobile-backend.herokuapp.com")
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideCardsApi(
        @Named("IokaRetrofit")
        retrofit: Retrofit
    ): CardsApi {
        return retrofit.create(CardsApi::class.java)
    }

    @Provides
    fun provideDemoApi(
        @Named("DemoRetrofit")
        retrofit: Retrofit
    ): DemoApi {
        return retrofit.create(DemoApi::class.java)
    }

}