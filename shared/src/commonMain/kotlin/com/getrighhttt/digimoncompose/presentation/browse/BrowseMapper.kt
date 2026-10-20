package com.getrighhttt.digimoncompose.presentation.browse

import com.getrighhttt.digimoncompose.domain.browse.model.BrowseFailure
import com.getrighhttt.digimoncompose.domain.browse.model.DigimonSummary
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseError
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard

internal fun DigimonSummary.toCard() = DigimonCard(id, name, image)
internal fun BrowseFailure.toUiError() = when (this) {
    BrowseFailure.InvalidResponse -> BrowseError.InvalidResponse
    BrowseFailure.RequestRejected -> BrowseError.RequestRejected
    BrowseFailure.Unavailable -> BrowseError.Unavailable
    BrowseFailure.InvalidPage -> BrowseError.InvalidPage
}
