package kz.ioka.android.ioka.domain.bindCard

internal sealed class CardBindingResultModel {

    companion object {
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_DECLINED = "DECLINED"
    }

    object Success : CardBindingResultModel()
    class Pending(
        val cardId: String,
        val actionUrl: String
    ) : CardBindingResultModel()

    class Declined(val cause: String) : CardBindingResultModel()

}

internal sealed class CardBindingStatusModel {

    companion object {
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_DECLINED = "DECLINED"
        const val STATUS_PENDING = "PENDING"
    }

    object Success : CardBindingStatusModel()
    class Failed(
        val cause: String?
    ) : CardBindingStatusModel()

}