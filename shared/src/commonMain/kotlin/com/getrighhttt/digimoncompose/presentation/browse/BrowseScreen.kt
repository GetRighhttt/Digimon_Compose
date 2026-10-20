package com.getrighhttt.digimoncompose.presentation.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.getrighhttt.digimoncompose.presentation.browse.model.AppendState
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseError
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseIntent
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseLoadState
import com.getrighhttt.digimoncompose.presentation.browse.model.BrowseState
import com.getrighhttt.digimoncompose.presentation.browse.model.DigimonCard
import com.getrighhttt.digimoncompose.presentation.browse.model.LevelOptionsState
import com.getrighhttt.digimoncompose.presentation.design.GuideButton
import com.getrighhttt.digimoncompose.presentation.design.GuideChip
import com.getrighhttt.digimoncompose.presentation.design.GuideEmblem
import com.getrighhttt.digimoncompose.presentation.design.GuideIcon
import com.getrighhttt.digimoncompose.presentation.design.GuideIconButton
import com.getrighhttt.digimoncompose.presentation.design.GuideIconKind
import com.getrighhttt.digimoncompose.presentation.design.GuideLoading
import com.getrighhttt.digimoncompose.presentation.design.GuideText
import com.getrighhttt.digimoncompose.presentation.design.LocalFieldGuideColors

/**
 * Browse has one scrolling content area; only its compact app header stays fixed.
 *
 * @author Stefan Bayne
 * @since 2026-10-19
 */
@Composable
internal fun BrowseScreen(
    state: BrowseState,
    gridState: LazyGridState,
    dispatch: (BrowseIntent) -> Unit,
    compact: Boolean,
    onToggleLayout: () -> Unit,
    onOpen: (DigimonCard) -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Column(Modifier.fillMaxSize().background(colors.background).safeDrawingPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GuideEmblem()
            GuideText(
                "Digimon",
                size = 22,
                weight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            GuideText("FIELD GUIDE", size = 10, weight = FontWeight.Bold, color = colors.secondary)
            GuideIconButton(GuideIconKind.Refresh, "Refresh catalog") {
                dispatch(BrowseIntent.Refresh)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(152.dp),
            state = gridState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "intro", span = { GridItemSpan(maxLineSpan) }) {
                SearchField(state.criteria.query) { dispatch(BrowseIntent.QueryChanged(it)) }
            }
            item(key = "filters", span = { GridItemSpan(maxLineSpan) }) {
                BrowseFilters(state.criteria.level, state.levels, dispatch)
            }

            item(key = "directory", span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GuideText("Directory", size = 20, weight = FontWeight.SemiBold)
                    GuideText(
                        "${state.items.size} loaded",
                        size = 12,
                        color = colors.secondary,
                        modifier = Modifier.weight(1f),
                    )
                    GuideIconButton(
                        if (compact) GuideIconKind.Grid else GuideIconKind.List,
                        if (compact) "Show artwork grid" else "Show compact list",
                        onToggleLayout,
                    )
                }
            }

            when (val load = state.load) {
                BrowseLoadState.Debouncing,
                BrowseLoadState.Loading,
                BrowseLoadState.Refreshing,
                -> {
                    item(key = "progress", span = { GridItemSpan(maxLineSpan) }) {
                        GuideLoading(
                            if (load == BrowseLoadState.Refreshing) {
                                "Refreshing the directory…"
                            } else {
                                "Finding Digimon…"
                            },
                        )
                    }
                }

                is BrowseLoadState.Failed -> {
                    item(key = "error", span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(colors.surface, RoundedCornerShape(20.dp))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            GuideText(
                                "A little interruption",
                                size = 18,
                                weight = FontWeight.SemiBold,
                            )
                            GuideText(errorText(load.error), size = 14, color = colors.secondary)
                            GuideButton("Try again", { dispatch(BrowseIntent.RetryReplacement) })
                        }
                    }
                }

                else -> {}
            }
            if (state.isEmpty) {
                item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        GuideText("No discoveries yet", size = 22, weight = FontWeight.SemiBold)
                        GuideText(
                            "Try another name or choose a different stage.",
                            color = colors.secondary,
                        )
                    }
                }
            }
            items(
                state.items,
                key = { "digimon-${it.id}" },
                span = { GridItemSpan(if (compact) maxLineSpan else 1) },
            ) { card ->
                DigimonEntry(card, compact) { onOpen(card) }
            }
            item(key = "pagination", span = { GridItemSpan(maxLineSpan) }) {
                BrowsePagination(state, dispatch)
            }
        }
    }
}

@Composable
private fun BrowseFilters(
    selectedLevel: String?,
    levels: LevelOptionsState,
    dispatch: (BrowseIntent) -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LazyRow(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                GuideChip("All stages", selectedLevel == null) {
                    dispatch(BrowseIntent.LevelChanged(null))
                }
            }
            val options = levels as? LevelOptionsState.Ready
            items(options?.values.orEmpty(), key = { it }) { level ->
                GuideChip(level, selectedLevel == level) {
                    dispatch(BrowseIntent.LevelChanged(level))
                }
            }
        }
        when (levels) {
            LevelOptionsState.Loading -> {
                GuideLoading("Finding stages…")
            }

            is LevelOptionsState.Failed -> {
                GuideText(
                    "Stage filters are unavailable.",
                    size = 13,
                    color = colors.secondary,
                )
                GuideButton("Retry stages", { dispatch(BrowseIntent.RetryLevels) })
            }

            else -> {}
        }
    }
}

@Composable
private fun BrowsePagination(
    state: BrowseState,
    dispatch: (BrowseIntent) -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    Column(
        Modifier.fillMaxWidth().padding(top = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val append = state.append) {
            is AppendState.Loading -> {
                GuideLoading("More discoveries on the way…")
            }

            is AppendState.Failed -> {
                GuideText(errorText(append.error), size = 14, color = colors.secondary)
                GuideButton(
                    "Retry loading more",
                    { dispatch(BrowseIntent.RetryAppend) },
                )
            }

            AppendState.Idle -> {
                if (state.load == BrowseLoadState.Ready && state.nextPage != null) {
                    GuideButton(
                        "Explore more Digimon",
                        { dispatch(BrowseIntent.LoadNextPage) },
                        Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onChange: (String) -> Unit,
) {
    val colors = LocalFieldGuideColors.current
    val keyboard = LocalSoftwareKeyboardController.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GuideIcon(GuideIconKind.Search, color = colors.secondary)
        BasicTextField(
            query,
            onChange,
            singleLine = true,
            modifier =
                Modifier.weight(1f).padding(vertical = 18.dp).semantics {
                    contentDescription = "Search Digimon by name"
                },
            textStyle = TextStyle(color = colors.text, fontSize = 17.sp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
            decorationBox = { field ->
                Box {
                    if (query.isEmpty()) {
                        GuideText(
                            "Search Digimon",
                            size = 17,
                            color = colors.secondary,
                        )
                    }
                    field()
                }
            },
        )
        if (query.isNotEmpty()) {
            GuideIconButton(GuideIconKind.Close, "Clear search") { onChange("") }
        } else {
            Spacer(Modifier.width(16.dp))
        }
    }
}

private fun errorText(error: BrowseError) =
    when (error) {
        BrowseError.InvalidResponse -> {
            "The directory returned an unreadable page. Please try again."
        }

        BrowseError.RequestRejected -> {
            "This search couldn't be completed. Try another name or stage."
        }

        BrowseError.Unavailable -> {
            "The directory couldn't be reached. Check your connection and try again."
        }

        BrowseError.InvalidPage -> {
            "This page couldn't be loaded. Your discoveries are still here."
        }
    }
