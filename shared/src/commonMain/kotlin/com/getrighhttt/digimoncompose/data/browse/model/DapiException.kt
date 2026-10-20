package com.getrighhttt.digimoncompose.data.browse.model

/** Data-layer failures carry no server body into presentation. */
internal sealed class DapiException : Exception() {
    class NotFound : DapiException()
    class Http(val status: Int) : DapiException()
    class InvalidResponse : DapiException()
    class Unavailable : DapiException()
}
