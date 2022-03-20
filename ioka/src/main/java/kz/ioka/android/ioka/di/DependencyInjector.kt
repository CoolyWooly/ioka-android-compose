package kz.ioka.android.ioka.di

import kz.ioka.android.ioka.BuildConfig
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.data.payment.PaymentApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object DependencyInjector {

    lateinit var cardApi: CardApi
    lateinit var cardInfoApi: CardInfoApi
    lateinit var paymentApi: PaymentApi

    fun createDependencies() {
        val okHttpClient = if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient
                .Builder()
                .build()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        cardApi = retrofit.create(CardApi::class.java)
        cardInfoApi = retrofit.create(CardInfoApi::class.java)
        paymentApi = retrofit.create(PaymentApi::class.java)
    }

}