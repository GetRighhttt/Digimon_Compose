package com.getrighhttt.digimoncompose.presentation.browse.model

/** Screen state contains presentation values; request identities reject obsolete work. */
data class BrowseState(
    val criteria: BrowseCriteria = BrowseCriteria(),
    val generation: Long = 0,
    val items: List<DigimonCard> = emptyList(),
    val load: BrowseLoadState = BrowseLoadState.Idle,
    val append: AppendState = AppendState.Idle,
    val nextPage: Int? = null,
    val activeReplacement: Long? = null,
    val levels: LevelOptionsState = LevelOptionsState.Loading,
) {
    val isEmpty: Boolean get() = load == BrowseLoadState.Ready && items.isEmpty()
}
