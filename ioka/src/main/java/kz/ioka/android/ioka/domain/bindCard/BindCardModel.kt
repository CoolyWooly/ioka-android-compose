package kz.ioka.android.ioka.domain.bindCard

sealed class CardBindingResultModel {

    companion object {
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_DECLINED = "DECLINED"
    }

    object Pending : CardBindingResultModel()
    object Success : CardBindingResultModel()
    class Declined(val cause: String) : CardBindingResultModel()

}