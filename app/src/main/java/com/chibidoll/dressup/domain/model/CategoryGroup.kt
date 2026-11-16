package com.chibidoll.dressup.domain.model

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

data class CategoryGroup(
    val id: String,
    val nameEn: String,
    val nameVi: String,
    val icon: ImageVector,
    val subcategories: List<CategorySubcategory>
) {
    fun getDisplayName(isVietnamese: Boolean = false): String {
        return if (isVietnamese && nameVi.isNotBlank()) nameVi else nameEn
    }

    companion object {
        fun defaultGroups(): List<CategoryGroup> = listOf(
            CategoryGroup(
                id = "appearance",
                nameEn = "Doll",
                nameVi = "Nhân vật",
                icon = Icons.Default.Face,
                subcategories = listOf(
                    CategorySubcategory("hair", "Hairstyle", "Kiểu tóc", Icons.Default.Face),
                    CategorySubcategory("eyes_line", "Eye Shape", "Dáng mắt", Icons.Default.Visibility),
                    CategorySubcategory("eyes_iris", "Eye Color", "Màu mắt", Icons.Default.Visibility),
                    CategorySubcategory("eyebrows", "Eyebrows", "Lông mày", Icons.Default.Gesture),
                    CategorySubcategory("mouth", "Mouth", "Khuôn miệng", Icons.Default.SentimentSatisfied),
                    CategorySubcategory("blush", "Blush", "Má hồng", Icons.Default.Favorite),
                    CategorySubcategory("body", "Skin & Body", "Cơ thể & Da", Icons.Default.Person)
                )
            ),
            CategoryGroup(
                id = "clothes",
                nameEn = "Clothes",
                nameVi = "Trang phục",
                icon = Icons.Default.Checkroom,
                subcategories = listOf(
                    CategorySubcategory("dress", "Dresses", "Váy liền", Icons.Default.DryCleaning),
                    CategorySubcategory("top", "Tops", "Áo", Icons.Default.Checkroom),
                    CategorySubcategory("bottom", "Bottoms", "Quần & Váy", Icons.Default.ShoppingBag),
                    CategorySubcategory("outerwear", "Jackets", "Áo khoác", Icons.Default.Layers),
                    CategorySubcategory("socks", "Socks", "Tất & Vớ", Icons.Default.RollerShades),
                    CategorySubcategory("shoes", "Shoes", "Giày", Icons.AutoMirrored.Filled.DirectionsWalk)
                )
            ),
            CategoryGroup(
                id = "accessories",
                nameEn = "Accessories",
                nameVi = "Phụ kiện",
                icon = Icons.Default.AutoAwesome,
                subcategories = listOf(
                    CategorySubcategory("hat", "Hats & Bows", "Mũ & Nơ", Icons.Default.Celebration),
                    CategorySubcategory("glasses", "Glasses", "Mắt kính", Icons.Default.AutoAwesome),
                    CategorySubcategory("ears", "Ears", "Tai thú & Elf", Icons.Default.Pets),
                    CategorySubcategory("tail", "Tails", "Đuôi thú", Icons.Default.Pets),
                    CategorySubcategory("wings", "Wings", "Đôi cánh", Icons.Default.Air),
                    CategorySubcategory("accessory", "Handheld & Deco", "Phụ kiện", Icons.Default.AutoAwesome)
                )
            ),
            CategoryGroup(
                id = "scene",
                nameEn = "Scene",
                nameVi = "Khung cảnh",
                icon = Icons.Default.Image,
                subcategories = listOf(
                    CategorySubcategory("background", "Wallpaper", "Hình nền", Icons.Default.Image)
                )
            )
        )
    }
}

data class CategorySubcategory(
    val id: String,
    val nameEn: String,
    val nameVi: String,
    val icon: ImageVector
) {
    fun getDisplayName(isVietnamese: Boolean = false): String {
        return if (isVietnamese && nameVi.isNotBlank()) nameVi else nameEn
    }
}

