package com.chibidoll.dressup.ui.dressup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chibidoll.dressup.domain.model.ItemCategory
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextSecondary

@Composable
fun CategoryBar(
    selectedCategory: ItemCategory,
    onCategorySelected: (ItemCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = ItemCategory.entries

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val shape = RoundedCornerShape(16.dp)

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(if (isSelected) 4.dp else 1.dp, shape = shape)
                    .clip(shape)
                    .background(if (isSelected) PastelPink else SurfaceWhite)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) AccentPink else Color(0xFFE8DEF8),
                        shape = shape
                    )
                    .bounceClick { onCategorySelected(category) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = stringResource(category.titleRes),
                    tint = if (isSelected) AccentPink else TextSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

