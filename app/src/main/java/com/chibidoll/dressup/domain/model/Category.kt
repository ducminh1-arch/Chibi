package com.chibidoll.dressup.domain.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RollerShades
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector
import com.chibidoll.dressup.R

enum class ItemCategory(
    val key: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    HAIR("hair", R.string.category_hair, Icons.Default.Face),
    FACE("face", R.string.category_face, Icons.Default.Mood),
    EYES("eyes", R.string.category_eyes, Icons.Default.Visibility),
    EYES_LINE("eyes_line", R.string.category_eyes_line, Icons.Default.Visibility),
    EYES_IRIS("eyes_iris", R.string.category_eyes_iris, Icons.Default.Visibility),
    EYEBROWS("eyebrows", R.string.category_eyebrows, Icons.Default.Gesture),
    MOUTH("mouth", R.string.category_mouth, Icons.Default.SentimentSatisfied),
    BLUSH("blush", R.string.category_blush, Icons.Default.Favorite),
    BODY("body", R.string.category_body, Icons.Default.Person),
    TOP("top", R.string.category_top, Icons.Default.Checkroom),
    BOTTOM("bottom", R.string.category_bottom, Icons.Default.ShoppingBag),
    DRESS("dress", R.string.category_dress, Icons.Default.DryCleaning),
    OUTERWEAR("outerwear", R.string.category_outerwear, Icons.Default.Layers),
    SOCKS("socks", R.string.category_socks, Icons.Default.RollerShades),
    SHOES("shoes", R.string.category_shoes, Icons.AutoMirrored.Filled.DirectionsWalk),
    GLASSES("glasses", R.string.category_glasses, Icons.Default.AutoAwesome),
    EARS("ears", R.string.category_ears, Icons.Default.Pets),
    TAIL("tail", R.string.category_tail, Icons.Default.Pets),
    WINGS("wings", R.string.category_wings, Icons.Default.Air),
    HAT("hat", R.string.category_hat, Icons.Default.Celebration),
    ACCESSORY("accessory", R.string.category_accessory, Icons.Default.AutoAwesome),
    BACKGROUND("background", R.string.category_background, Icons.Default.Image);

    companion object {
        fun fromKey(key: String): ItemCategory =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: ACCESSORY
    }
}
