package kz.ioka.android.ioka.domain.saveCard

internal sealed class SaveCardResultModel {

    companion object {
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_DECLINED = "DECLINED"
    }

    object Success : SaveCardResultModel()
    class Pending(
        val cardId: String,
        val actionUrl: String
    ) : SaveCardResultModel()

    class Declined(val cause: String) : SaveCardResultModel()

}

internal sealed class SaveCardStatusModel {

    companion object {
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_DECLINED = "DECLINED"
        const val STATUS_PENDING = "PENDING"
    }

    object Success : SaveCardStatusModel()
    class Failed(
        val cause: String?
    ) : SaveCardStatusModel()

}