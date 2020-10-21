package com.mustafa.tandem.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val isLastPage: Boolean
) {
    companion object {
        fun <T> success(data: T?, isLastPage: Boolean): Resource<T> {
            return Resource(Status.SUCCESS, data, null, isLastPage)
        }

        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg, false)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null, false)
        }
    }
}
