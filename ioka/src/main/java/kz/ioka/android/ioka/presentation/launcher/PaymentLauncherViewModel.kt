package kz.ioka.android.ioka.presentation.launcher

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kz.ioka.android.ioka.util.ViewAction

internal class PaymentLauncherViewModelFactory(
    private val behavior: PaymentLauncherBehavior
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PaymentLauncherViewModel(behavior) as T
    }
}

internal class PaymentLauncherViewModel(
    private val behavior: PaymentLauncherBehavior
) : ViewModel() {

    val titleRes = behavior.titleRes

    private val _progress = MutableLiveData(false)
    val progress = _progress as LiveData<Boolean>

    private val _action = MutableLiveData<ViewAction>()
    val action = _action as LiveData<ViewAction>

    init {
        behavior
            .observeProgress()
            .onEach {
                _progress.postValue(it)
            }
            .launchIn(viewModelScope)
    }

    fun onUiShown() {
        viewModelScope.launch(Dispatchers.IO) {
            behavior.doOnLoading()
            _action.postValue(behavior.doAfterLoading())
        }
    }

}