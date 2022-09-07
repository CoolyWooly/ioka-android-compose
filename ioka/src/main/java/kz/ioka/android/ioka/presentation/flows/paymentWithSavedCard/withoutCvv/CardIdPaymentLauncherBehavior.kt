package kz.ioka.android.ioka.presentation.flows.paymentWithSavedCard.withoutCvv

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.common.Amount
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper
import kz.ioka.android.ioka.domain.order.OrderModel
import kz.ioka.android.ioka.domain.order.OrderRepositoryImpl
import kz.ioka.android.ioka.domain.payment.PaymentRepositoryImpl
import kz.ioka.android.ioka.presentation.launcher.PaymentLauncherBehavior
import kz.ioka.android.ioka.presentation.result.ErrorResultLauncher
import kz.ioka.android.ioka.presentation.result.ResultFragment
import kz.ioka.android.ioka.presentation.result.SuccessResultLauncher
import kz.ioka.android.ioka.util.ViewAction
import kz.ioka.android.ioka.util.getOrderId
import kz.ioka.android.ioka.util.replaceFragment
import kz.ioka.android.ioka.viewBase.BaseActivity

@Parcelize
internal class CardIdPaymentLauncherBehavior(
    val orderToken: String,
    val cardId: String
) : PaymentLauncherBehavior {

    @IgnoredOnParcel
    private val orderRepository = OrderRepositoryImpl(DependencyInjector.orderApi)

    @IgnoredOnParcel
    private val paymentRepository = PaymentRepositoryImpl(DependencyInjector.paymentApi)

    @IgnoredOnParcel
    private var orderModel: OrderModel? = null

    @IgnoredOnParcel
    private var isPaymentCreatedSuccessfully: Boolean? = null

    override val titleRes: Int
        get() = R.string.ioka_common_processing_payment

    override fun observeProgress(): Flow<Boolean> = flowOf(true)

    override suspend fun doOnLoading() {
        val orderResponse = orderRepository.getOrderById(orderToken.getOrderId())

        if (orderResponse is ResultWrapper.Success) {
            orderModel = orderResponse.value

            val paymentResponse = paymentRepository.createPaymentWithCardId(
                orderToken.getOrderId(),
                Config.apiKey,
                cardId
            )

            isPaymentCreatedSuccessfully = paymentResponse is ResultWrapper.Success
        }
    }

    override fun doAfterLoading(): ViewAction {
        return if (isPaymentCreatedSuccessfully != true) {
            ViewAction {
                (it as? BaseActivity)?.supportFragmentManager?.replaceFragment(
                    ResultFragment.getInstance(
                        ErrorResultLauncher(
                            subtitle = it.getString(R.string.ioka_common_server_error)
                        )
                    )
                )
            }
        } else {
            ViewAction {
                (it as? BaseActivity)?.supportFragmentManager?.replaceFragment(
                    ResultFragment.getInstance(
                        SuccessResultLauncher(
                            subtitle = it.getString(
                                R.string.ioka_result_success_payment_subtitle,
                                orderModel?.externalId
                            ),
                            amount = orderModel?.amount ?: Amount.ZERO
                        )
                    )
                )
            }
        }
    }
}