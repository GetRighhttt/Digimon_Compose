package com.getrighhttt.digimoncompose.data.browse.model

/**
 * Classifies request failures before repositories map them to domain errors.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal sealed class DapiException : Exception() {
    class NotFound : DapiException()

    class Http(
        val status: Int,
    ) : DapiException()

    class InvalidResponse : DapiException()

    class Unavailable : DapiException()
}
