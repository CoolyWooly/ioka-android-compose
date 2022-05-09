package kz.ioka.android.ioka.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.api.FlowResult.*

/**
 * Класс используется для отправки результата до активити мерчанта
 * Любой флоу имеет три варианта исхода:
 *
 * [Succeeded] - флоу завершен успешно, пример: карта сохранена успешно
 * [Failed] - флоу завершен неуспешно, пример: не удалось оплатить платеж
 *      @param [cause] - причина ошибки
 * [Cancelled] - пользователь завершил флоу вручную, пример: вернулся на страницу мерчанта по нажатию "Назад"
 */
sealed class FlowResult : Parcelable {

    @Parcelize
    object Succeeded : FlowResult()

    @Parcelize
    class Failed(val cause: String) : FlowResult()

    @Parcelize
    object Cancelled : FlowResult()

}