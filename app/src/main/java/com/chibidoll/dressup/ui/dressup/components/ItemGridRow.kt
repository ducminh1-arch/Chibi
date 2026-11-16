package com.chibidoll.dressup.ui.dressup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextSecondary

private const val ASSET_PREFIX = "file:///android_asset/"

@Composable
fun ItemGridView(
    items: List<CatalogItem>,
    currentLook: CharacterLook,
    onItemClick: (CatalogItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No items in this category",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            val isEquipped = currentLook.isEquipped(item)
            val cardShape = RoundedCornerShape(14.dp)

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .shadow(if (isEquipped) 5.dp else 1.dp, shape = cardShape)
                    .clip(cardShape)
                    .background(if (isEquipped) PastelPink.copy(alpha = 0.45f) else SurfaceWhite)
                    .border(
                        width = if (isEquipped) 2.5.dp else 1.dp,
                        color = if (isEquipped) AccentPink else Color(0xFFEEE5F4),
                        shape = cardShape
                    )
                    .bounceClick { onItemClick(item) }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Item thumbnail
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(ASSET_PREFIX + item.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.nameEn,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Equipped check badge
                if (isEquipped) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(AccentPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Equipped",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Colorable indicator
                if (item.colorable) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFAB47BC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Colorable",
                            tint = Color.White,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }

                // Locked badge
                if (item.locked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }
}
