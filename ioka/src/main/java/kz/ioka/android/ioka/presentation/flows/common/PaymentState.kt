package kz.ioka.android.ioka.presentation.flows.common

internal sealed class PaymentState {

    object DEFAULT : PaymentState()
    object DISABLED : PaymentState()
    object LOADING : PaymentState()
    object SUCCESS : PaymentState()
    class FAILED(val cause: String? = null) : PaymentState()
    class ERROR(val cause: String? = null) : PaymentState()

    class PENDING(val actionUrl: String) : PaymentState()
}