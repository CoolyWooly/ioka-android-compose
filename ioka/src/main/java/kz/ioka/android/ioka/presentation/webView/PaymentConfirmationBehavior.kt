package kz.ioka.android.ioka.presentation.webView

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.payment.PaymentModel
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl

@Parcelize
internal class PaymentConfirmationBehavior(
    override val toolbarTitleRes: Int = R.string.ioka_common_payment_confirmation,
    private val url: String,
    private val orderToken: String,
    private val paymentId: String
) : WebViewBehavior {

    @IgnoredOnParcel
    private val paymentRepository = PaymentRepositoryImpl(DependencyInjector.paymentApi)

    @IgnoredOnParcel
    private val progressFlow = MutableStateFlow(false)

    override val actionUrl: String
        get() = String.format("%s?return_url=https://ioka.kz", url)

    override fun observeProgress() = progressFlow

    override suspend fun onActionFinished(): ResultState {
        progressFlow.value = true

        val payment = paymentRepository.isPaymentSuccessful(
            Config.apiKey,
            orderToken,
            paymentId
        )

        progressFlow.value = false

        return when (payment) {
            is ResultWrapper.Success -> {
                processSuccess(payment.value)
            }
            is ResultWrapper.IokaError -> {
                ResultState.Fail(payment.message)
            }
            else -> ResultState.Fail()
        }
    }

    private fun processSuccess(payment: PaymentModel): ResultState {
        return when (payment) {
            is PaymentModel.Success -> ResultState.Success
            is PaymentModel.Declined -> ResultState.Fail(payment.message)
            else -> ResultState.Fail()
        }
    }

}