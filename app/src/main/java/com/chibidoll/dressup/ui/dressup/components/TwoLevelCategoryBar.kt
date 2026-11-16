package com.chibidoll.dressup.ui.dressup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidoll.dressup.domain.model.CategoryGroup
import com.chibidoll.dressup.domain.model.CategorySubcategory
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.PastelPurpleDark
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun TwoLevelCategoryBar(
    groups: List<CategoryGroup>,
    selectedGroup: CategoryGroup,
    selectedSubcategory: CategorySubcategory,
    onGroupSelected: (CategoryGroup) -> Unit,
    onSubcategorySelected: (CategorySubcategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVietnamese = remember { Locale.getDefault().language == "vi" }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Level 1: Category Groups (Top Row)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(groups, key = { it.id }) { group ->
                val isSelected = group.id == selectedGroup.id
                val shape = RoundedCornerShape(16.dp)

                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .shadow(if (isSelected) 3.dp else 1.dp, shape = shape)
                        .clip(shape)
                        .background(if (isSelected) PastelPink else SurfaceWhite)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AccentPink else Color(0xFFE8DEF8),
                            shape = shape
                        )
                        .bounceClick { onGroupSelected(group) }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = group.icon,
                            contentDescription = group.getDisplayName(isVietnamese),
                            tint = if (isSelected) AccentPink else PastelPurpleDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = group.getDisplayName(isVietnamese),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AccentPink else TextPrimary
                        )
                    }
                }
            }
        }

        // Level 2: Subcategories of the selected group (Bottom Row)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedGroup.subcategories, key = { it.id }) { subcat ->
                val isSelected = subcat.id == selectedSubcategory.id
                val shape = RoundedCornerShape(14.dp)

                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .shadow(if (isSelected) 2.dp else 0.dp, shape = shape)
                        .clip(shape)
                        .background(if (isSelected) AccentPink else SurfaceWhite.copy(alpha = 0.9f))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AccentPink else PastelPurple.copy(alpha = 0.5f),
                            shape = shape
                        )
                        .bounceClick { onSubcategorySelected(subcat) }
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = subcat.icon,
                            contentDescription = subcat.getDisplayName(isVietnamese),
                            tint = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = subcat.getDisplayName(isVietnamese),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

