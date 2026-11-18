package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.domain.browse.model.BrowseFailure
import com.getrighhttt.digimoncompose.domain.browse.model.DigimonSummary
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseError
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard

/**
 * Converts domain results into the cards and errors the catalog displays.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
internal fun DigimonSummary.toCard() = DigimonCard(id, name, image)

internal fun BrowseFailure.toUiError() =
    when (this) {
        BrowseFailure.InvalidResponse -> BrowseError.InvalidResponse
        BrowseFailure.RequestRejected -> BrowseError.RequestRejected
        BrowseFailure.Unavailable -> BrowseError.Unavailable
        BrowseFailure.InvalidPage -> BrowseError.InvalidPage
    }
