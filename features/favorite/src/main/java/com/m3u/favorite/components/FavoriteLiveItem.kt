package com.m3u.favorite.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.m3u.data.entity.Live
import com.m3u.features.favorite.R
import com.m3u.ui.components.basic.M3UColumn
import com.m3u.ui.local.LocalSpacing
import com.m3u.ui.local.LocalTheme
import java.net.URI

@Composable
internal fun FavoriteLiveItem(
    live: Live,
    subscriptionTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scheme = remember(live) {
        URI(live.url).scheme
            .orEmpty()
            .ifEmpty { context.getString(R.string.scheme_unknown) }
            .uppercase()
    }
    Card(
        shape = RectangleShape
    ) {
        M3UColumn(
            modifier = modifier.clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            )
        ) {
            CompositionLocalProvider(
                LocalContentColor provides LocalTheme.current.tint
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.extraSmall)
                ) {
                    BrightIcon(
                        text = subscriptionTitle,
                        color = LocalTheme.current.tint,
                        contentColor = LocalTheme.current.onTint,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Link,
                                contentDescription = null,
                                modifier = Modifier.size(LocalSpacing.current.medium)
                            )
                        }
                    )
                    Text(
                        text = live.title,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.extraSmall)
            ) {
                BrightIcon(text = scheme)
                CompositionLocalProvider(
                    LocalContentAlpha provides 0.6f
                ) {
                    Text(
                        text = live.url,
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
