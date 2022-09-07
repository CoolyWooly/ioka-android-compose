package kz.ioka.android.ioka.viewBase

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import kz.ioka.android.ioka.presentation.webView.WebViewBehavior
import kz.ioka.android.ioka.presentation.webView.WebViewFragment

/**
 * Интерфейс для вьюшек (фрагментов, активити), которые вызывают проверку 3DSecure
 * Проверка 3DSecure воспроизводится в вебвью
 */
internal interface ThreeDSecurable {

    /**
     * Return type метода registerForActivityResult
     */
    val activityResultLauncher: ActivityResultLauncher<Intent>

    /**
     * Вызывается, если проверка прошла успешно
     */
    fun onSuccessfulAttempt()

    /**
     * Вызывается, если проверка прошла неуспешно
     * @param cause
     *          причина неуспешной проверки
     */
    fun onFailedAttempt(cause: String? = null)

    /**
     * Возвращает обработчика результата активности проверки
     * @return обработчик результата активности проверки 3DSecure,
     *          используется для создания поля `activityResultLauncher`
     */
    fun activityResultCallback(): ActivityResultCallback<ActivityResult> =
        ActivityResultCallback<ActivityResult> {
            if (it.resultCode == WebViewFragment.RESULT_SUCCESS) {
                onSuccessfulAttempt()
            } else if (it.resultCode == WebViewFragment.RESULT_FAIL) {
                onFailedAttempt()
            }
        }

    /**
     * Вызывайте этот метод из дочернего класса в момент, когда нужно запустить проверку 3DSecure
     */
    fun onActionRequired(
        context: Context,
        behavior: WebViewBehavior
    ) {
        val intent = WebViewFragment.getInstance(behavior)

//        activityResultLauncher.launch(intent)
    }
}