package kz.ioka.android.ioka.presentation.result

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import kz.ioka.android.ioka.R

internal abstract class ResultLauncher(
    @DrawableRes open val statusIconRes: Int,
    @StringRes open val titleRes: Int,
    @ColorRes open val titleColorRes: Int,
    open val subtitle: String,
    open val amount: Int,
    @StringRes open val btnTitleRes: Int
) : Parcelable

@Parcelize
internal class SuccessResultLauncher(
    override val statusIconRes: Int = R.drawable.ic_success,
    override val titleRes: Int = R.string.success_result_title,
    override val titleColorRes: Int = R.color.ioka_color_static_green,
    override val subtitle: String,
    override val amount: Int,
    override val btnTitleRes: Int = R.string.success_result_button
) : ResultLauncher(statusIconRes, titleRes, titleColorRes, subtitle, amount, btnTitleRes)

@Parcelize
internal class ErrorResultLauncher(
    override val statusIconRes: Int = R.drawable.ic_error,
    override val titleRes: Int = R.string.error_result_title,
    override val titleColorRes: Int = R.color.ioka_color_text_primary,
    override val subtitle: String,
    override val amount: Int,
    override val btnTitleRes: Int = R.string.error_result_button
) : ResultLauncher(statusIconRes, titleRes, titleColorRes, subtitle, amount, btnTitleRes)
