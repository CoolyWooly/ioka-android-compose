package kz.ioka.android.ioka.flows.payWithCard

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kz.ioka.android.ioka.viewBase.BaseActivity
import javax.inject.Inject

@HiltViewModel
class PayWithCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val launcher = savedStateHandle.get<PayWithCardLauncher>(BaseActivity.LAUNCHER)

    val price = launcher?.price

    fun onCardPanEntered(cardPan: String) {

    }

    fun onExpireDateEntered(expireDate: String) {
        TODO("Not yet implemented")
    }

}

sealed class PayState {

    object Default : PayState()
    object Disabled : PayState()
    object Loading : PayState()

}