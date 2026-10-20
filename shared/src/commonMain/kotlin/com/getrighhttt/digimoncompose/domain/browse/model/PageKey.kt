package com.getrighhttt.digimoncompose.domain.browse.model

data class PageKey(val index: Int) {
    init { require(index >= 0) }
}
