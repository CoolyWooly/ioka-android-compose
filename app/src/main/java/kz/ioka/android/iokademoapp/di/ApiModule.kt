package kz.ioka.android.iokademoapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.ioka.android.iokademoapp.data.remote.DemoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    fun provideGson(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideDemoRetrofit(gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ioka-example-mobile-backend.herokuapp.com")
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideDemoApi(
        retrofit: Retrofit
    ): DemoApi {
        return retrofit.create(DemoApi::class.java)
    }

}