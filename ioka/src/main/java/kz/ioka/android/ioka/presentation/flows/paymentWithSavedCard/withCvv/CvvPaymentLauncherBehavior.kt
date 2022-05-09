package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withCvv

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.CardDvo
import kz.ioka.android.ioka.api.Configuration
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.order.OrderRepositoryImpl
import kz.ioka.android.ioka.presentation.flows.common.OrderDvo
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherBehavior
import kz.ioka.android.ioka.util.ViewAction
import kz.ioka.android.ioka.util.getOrderId
import kz.ioka.android.ioka.util.showErrorToast

@Parcelize
internal class CvvPaymentLauncherBehavior(
    private val orderToken: String,
    private val cardDvo: CardDvo,
    private val configuration: Configuration? = null
) : PaymentLauncherBehavior {

    @IgnoredOnParcel
    private val orderRepository = OrderRepositoryImpl(DependencyInjector.orderApi)

    @IgnoredOnParcel
    private var order: OrderDvo? = null

    @IgnoredOnParcel
    private val progressFlow = MutableStateFlow(true)

    override val titleRes: Int get() = R.string.ioka_common_processing_payment

    override fun observeProgress(): Flow<Boolean> = progressFlow

    override suspend fun doOnLoading() {
        val orderResponse = orderRepository.getOrderById(orderToken.getOrderId())

        if (orderResponse is ResultWrapper.Success)
            order = OrderDvo(
                orderResponse.value.externalId,
                orderResponse.value.amount
            )
    }

    override fun doAfterLoading(): ViewAction {
        return if (order == null) {
            ViewAction {
                it.showErrorToast(it.getString(R.string.ioka_common_server_error))
                it.finish()
            }
        } else {
            progressFlow.value = false

            ViewAction {
                val newFragment: PayWithCvvFragment = PayWithCvvFragment.newInstance(
                    PayWithCvvLauncher(
                        orderToken,
                        order!!,
                        cardDvo.cardId,
                        cardDvo.cardNumber,
                        cardDvo.cardType,
                        configuration
                    )
                )
                newFragment.show(
                    it.supportFragmentManager,
                    newFragment::class.simpleName
                )
            }
        }
    }
}