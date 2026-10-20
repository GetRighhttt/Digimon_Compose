package com.getrighhttt.digimoncompose.domain.browse.model

/**
 * Identifies a nonnegative catalog page within the current search criteria.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
data class PageKey(val index: Int) {
    init { require(index >= 0) }
}
