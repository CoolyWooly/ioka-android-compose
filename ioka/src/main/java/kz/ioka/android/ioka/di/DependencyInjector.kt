package kz.ioka.android.ioka.di

import android.util.Log
import kz.ioka.android.ioka.BuildConfig
import kz.ioka.android.ioka.data.card.CardApi
import kz.ioka.android.ioka.data.cardInfo.CardInfoApi
import kz.ioka.android.ioka.data.payment.PaymentApi
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ProtocolException

internal object DependencyInjector {

    lateinit var cardApi: CardApi
    lateinit var cardInfoApi: CardInfoApi
    lateinit var paymentApi: PaymentApi

    fun createDependencies() {
        val okHttpClientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder
                .addInterceptor(loggingInterceptor)
                .build()
        }

        okHttpClientBuilder
            .addNetworkInterceptor { chain ->
                val response: Response = try {
                    chain.proceed(chain.request())
                } catch (e: ProtocolException) {
                    Log.d("204", e.message ?: "CHECK")
                    Response.Builder()
                        .request(chain.request())
                        .code(204)
                        .protocol(Protocol.HTTP_1_1)
                        .build()
                }
                response
            }

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())
            .build()

        cardApi = retrofit.create(CardApi::class.java)
        cardInfoApi = retrofit.create(CardInfoApi::class.java)
        paymentApi = retrofit.create(PaymentApi::class.java)
    }

}