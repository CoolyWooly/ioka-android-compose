package kz.ioka.android.ioka.di

import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.data.order.OrderApi
import kz.ioka.android.ioka.data.payment.PaymentApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object DependencyInjector {

    const val DEBUG_BASE_URL = "https://stage-api.ioka.kz/"
    const val RELEASE_BASE_URL = "https://api.ioka.kz/"

    private val baseUrl by lazy {
        if (Config.isDebug) DEBUG_BASE_URL
        else RELEASE_BASE_URL
    }

    private val loggingInterceptor by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val okHttpClient by lazy {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
        okHttpClientBuilder.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val cardApi: CardApi by lazy {
        retrofit.create(CardApi::class.java)
    }

    val cardInfoApi: CardInfoApi by lazy {
        retrofit.create(CardInfoApi::class.java)
    }

    val paymentApi: PaymentApi by lazy {
        retrofit.create(PaymentApi::class.java)
    }

    val orderApi: OrderApi by lazy {
        retrofit.create(OrderApi::class.java)
    }

}