package kz.ioka.android.ioka.presentation.result

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R
import kz.ioka.android.ioka.api.FlowResult
import kz.ioka.android.ioka.domain.common.Amount
import java.math.BigDecimal

internal abstract class ResultLauncher(
    @DrawableRes open val statusIconRes: Int,
    @StringRes open val titleRes: Int,
    @ColorRes open val titleColorRes: Int,
    open val subtitle: String,
    open val amount: Amount,
    @StringRes open val btnTitleRes: Int,
    open val flowResult: FlowResult
) : Parcelable

@Parcelize
internal class SuccessResultLauncher(
    override val statusIconRes: Int = R.drawable.ioka_ic_success,
    override val titleRes: Int = R.string.ioka_result_success_payment_title,
    override val titleColorRes: Int = R.color.ioka_color_success,
    override val subtitle: String,
    override val amount: Amount,
    override val btnTitleRes: Int = R.string.ioka_common_understand,
    override val flowResult: FlowResult = FlowResult.Succeeded
) : ResultLauncher(
    statusIconRes,
    titleRes,
    titleColorRes,
    subtitle,
    amount,
    btnTitleRes,
    flowResult
)

@Parcelize
internal class ErrorResultLauncher(
    override val statusIconRes: Int = R.drawable.ioka_ic_error,
    override val titleRes: Int = R.string.ioka_result_failed_payment_title,
    override val titleColorRes: Int = R.color.ioka_color_text,
    override val subtitle: String,
    override val amount: Amount = Amount(BigDecimal.ZERO),
    override val btnTitleRes: Int = R.string.ioka_common_try_again,
    override val flowResult: FlowResult = FlowResult.Failed(subtitle)
) : ResultLauncher(
    statusIconRes,
    titleRes,
    titleColorRes,
    subtitle,
    amount,
    btnTitleRes,
    flowResult
)
