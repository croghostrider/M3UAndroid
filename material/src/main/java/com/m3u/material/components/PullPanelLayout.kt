package com.m3u.material.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.offset
import kotlin.math.roundToInt

enum class PullPanelLayoutValue {
    EXPANDED, COLLAPSED
}

interface PullPanelLayoutState {
    val value: PullPanelLayoutValue
    fun expand()
    fun collapse()
}

@Composable
fun rememberPullPanelLayoutState(
    initialValue: PullPanelLayoutValue = PullPanelLayoutValue.COLLAPSED
): PullPanelLayoutState = remember(initialValue) {
    PullPanelLayoutStateImpl(initialValue)
}

@Composable
fun PullPanelLayout(
    panel: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: PullPanelLayoutState = rememberPullPanelLayoutState(),
    aspectRatio: Float = 10 / 7f,
    enabled: Boolean = true,
    onOffsetChanged: (Float) -> Unit = {},
    onValueChanged: (PullPanelLayoutValue) -> Unit = {}
) {
    var savedMaxHeight by remember { mutableIntStateOf(0) }
    var savedMaxWidth by remember { mutableIntStateOf(0) }
    var offset: Float by remember(state, savedMaxHeight) {
        mutableFloatStateOf(
            when (state.value) {
                PullPanelLayoutValue.EXPANDED -> savedMaxHeight.toFloat()
                PullPanelLayoutValue.COLLAPSED -> 0f
            }
        )
    }
    val currentOffset by animateFloatAsState(
        targetValue = offset,
        label = "offset"
    )
    LaunchedEffect(enabled) {
        if (!enabled) {
            offset = 0f
            onOffsetChanged(0f)
            onValueChanged(PullPanelLayoutValue.COLLAPSED)
            state.collapse()
        }
    }
    SubcomposeLayout(
        modifier
            .draggable(
                orientation = Orientation.Vertical,
                enabled = enabled,
                state = rememberDraggableState { delta ->
                    offset = (offset - delta)
                        .coerceAtLeast(0f)
                        .also(onOffsetChanged)
                },
                onDragStopped = {
                    offset = if (offset <= savedMaxWidth * aspectRatio / 2) {
                        onValueChanged(PullPanelLayoutValue.COLLAPSED)
                        state.collapse()
                        0f
                    } else {
                        onValueChanged(PullPanelLayoutValue.EXPANDED)
                        state.expand()
                        savedMaxWidth * aspectRatio
                    }.also(onOffsetChanged)
                }
            )
    ) { constraints ->
        val maxHeight = constraints.maxHeight
        val maxWidth = constraints.maxWidth
        savedMaxHeight = maxHeight
        savedMaxWidth = maxWidth
        val panelLayerPlaceable = subcompose(PullPanelLayoutValue.EXPANDED, panel)
            .first()
            .measure(
                constraints
                    .copy(
                        maxHeight = currentOffset
                            .roundToInt()
                            .coerceAtMost(
                                (maxWidth * aspectRatio).roundToInt()
                            )
                    )
            )

        val contentPlaceable = subcompose(PullPanelLayoutValue.COLLAPSED, content)
            .first()
            .measure(
                constraints
                    .offset(
                        vertical = -currentOffset
                            .roundToInt()
                            .coerceAtMost((maxWidth * aspectRatio).roundToInt())
                    )
            )

        layout(maxWidth, maxHeight) {
            contentPlaceable.placeRelative(0, 0)
            panelLayerPlaceable.placeRelative(0, contentPlaceable.height)
        }
    }
}

private class PullPanelLayoutStateImpl(
    initialValue: PullPanelLayoutValue = PullPanelLayoutValue.COLLAPSED
) : PullPanelLayoutState {
    override var value: PullPanelLayoutValue by mutableStateOf(initialValue)
    override fun expand() {
        value = PullPanelLayoutValue.EXPANDED
    }

    override fun collapse() {
        value = PullPanelLayoutValue.COLLAPSED
    }
}

