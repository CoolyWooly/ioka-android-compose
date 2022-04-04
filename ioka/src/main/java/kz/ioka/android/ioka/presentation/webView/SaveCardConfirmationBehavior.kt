package kz.ioka.android.ioka.presentation.webView

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.Config
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.di.DependencyInjector
import kz.ioka.android.ioka.domain.saveCard.SaveCardStatusModel
import kz.ioka.android.ioka.domain.saveCard.CardRepositoryImpl
import kz.ioka.android.ioka.domain.errorHandler.ResultWrapper

@Parcelize
class SaveCardConfirmationBehavior(
    override val toolbarTitleRes: Int = R.string.ioka_common_payment_confirmation,
    private val url: String,
    private val customerToken: String,
    private val cardId: String
) : WebViewBehavior {

    @IgnoredOnParcel
    private val cardRepository = CardRepositoryImpl(DependencyInjector.cardApi)

    @IgnoredOnParcel
    private val progressFlow = MutableStateFlow(false)

    override val actionUrl: String
        get() = String.format("%s?return_url=https://ioka.kz", url)

    override fun observeProgress() = progressFlow

    override suspend fun onActionFinished(): Boolean {
        progressFlow.value = true

        val payment = cardRepository.getSaveCardStatus(
            customerToken,
            Config.apiKey,
            cardId,
        )

        progressFlow.value = false
        return payment is ResultWrapper.Success && payment.value is SaveCardStatusModel.Success
    }


}