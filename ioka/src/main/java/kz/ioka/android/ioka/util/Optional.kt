package kz.ioka.android.ioka.util

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 */
internal data class Optional<T>(private val reference: T?) {
    fun isPresent() = reference != null
    fun isNotPresent() = !isPresent()
    fun get() = reference!!
    fun getOrDefault(default: T) = reference ?: default
    fun <F> get(block: (T?) -> F) = block(reference)
    fun getIfNull() = reference

    fun orElseThrow(exception: Exception) = reference ?: throw exception

    fun <F> map(block: (T) -> F?): Optional<F> {
        return getIfNull()?.let { block(it) }.optional()
    }

    companion object {
        fun <T> empty() = Optional<T>(null)
        fun <T> of(t: T?) = Optional(t)
    }

}

internal fun <T> T?.optional(): Optional<T> =
    Optional(this)
