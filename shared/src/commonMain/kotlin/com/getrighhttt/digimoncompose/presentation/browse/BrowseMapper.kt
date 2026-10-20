package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.domain.browse.model.BrowseFailure
import com.getrighhttt.digimoncompose.domain.browse.model.DigimonSummary
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseError
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard

/**
 * Maps domain catalog summaries and failures into presentation-owned cards and errors.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun DigimonSummary.toCard() = DigimonCard(id, name, image)
internal fun BrowseFailure.toUiError() = when (this) {
    BrowseFailure.InvalidResponse -> BrowseError.InvalidResponse
    BrowseFailure.RequestRejected -> BrowseError.RequestRejected
    BrowseFailure.Unavailable -> BrowseError.Unavailable
    BrowseFailure.InvalidPage -> BrowseError.InvalidPage
}
