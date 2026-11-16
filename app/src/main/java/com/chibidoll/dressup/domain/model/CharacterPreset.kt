package com.chibidoll.dressup.domain.model

data class CharacterPreset(
    val id: String,
    val nameEn: String,
    val nameVi: String,
    val descriptionEn: String,
    val descriptionVi: String,
    val bodyItemId: String = "body_fair",
    val faceItemId: String = "face_purple",
    val hairItemId: String,
    val eyesItemId: String = "eyes_purple",
    val eyesLineItemId: String = "eyes_line_purple",
    val eyesIrisItemId: String = "eyes_iris_purple",
    val eyebrowsItemId: String = "brow_gentle",
    val mouthItemId: String = "mouth_smile",
    val blushItemId: String? = null,
    val defaultClothesItemIds: List<String> = emptyList(),
    val defaultBackgroundId: String? = "bg_pastel_dream"
) {
    fun getDisplayName(isVietnamese: Boolean = false): String {
        return if (isVietnamese && nameVi.isNotBlank()) nameVi else nameEn
    }

    fun getDescription(isVietnamese: Boolean = false): String {
        return if (isVietnamese && descriptionVi.isNotBlank()) descriptionVi else descriptionEn
    }

    companion object {
        fun defaultPresets(): List<CharacterPreset> = listOf(
            CharacterPreset(
                id = "preset_luna",
                nameEn = "Luna",
                nameVi = "Bé Luna",
                descriptionEn = "Dreamy lilac twin tails & purple sparkle eyes",
                descriptionVi = "Tóc tím mộng mơ và đôi mắt lấp lánh",
                bodyItemId = "body_fair",
                faceItemId = "face_purple",
                hairItemId = "hair_lilac_twin",
                eyesItemId = "eyes_purple",
                eyesLineItemId = "eyes_line_purple",
                eyesIrisItemId = "eyes_iris_purple",
                eyebrowsItemId = "brow_gentle",
                mouthItemId = "mouth_smile",
                blushItemId = "blush_pink",
                defaultClothesItemIds = listOf("top_lavender", "bot_navy_skirt", "shoes_mary_jane"),
                defaultBackgroundId = "bg_dream"
            ),
            CharacterPreset(
                id = "preset_sakura",
                nameEn = "Sakura",
                nameVi = "Bé Sakura",
                descriptionEn = "Playful pink waves & charming winking smile",
                descriptionVi = "Tóc hồng đáng yêu và nụ cười nháy mắt",
                bodyItemId = "body_fair",
                faceItemId = "face_wink",
                hairItemId = "hair_pink_wavy",
                eyesItemId = "eyes_wink_pink",
                eyesLineItemId = "eyes_line_wink",
                eyesIrisItemId = "eyes_iris_pink",
                eyebrowsItemId = "brow_playful",
                mouthItemId = "mouth_happy_open",
                blushItemId = "blush_hearts",
                defaultClothesItemIds = listOf("dress_princess", "shoes_sneakers", "hat_giant_bow"),
                defaultBackgroundId = "bg_hearts"
            ),
            CharacterPreset(
                id = "preset_sunny",
                nameEn = "Sunny",
                nameVi = "Bé Sunny",
                descriptionEn = "Energetic blonde pigtails & curious amber eyes",
                descriptionVi = "Tóc vàng năng động và đôi mắt tròn xoe",
                bodyItemId = "body_fair",
                faceItemId = "face_brown",
                hairItemId = "hair_blonde_twin",
                eyesItemId = "eyes_brown",
                eyesLineItemId = "eyes_line_brown",
                eyesIrisItemId = "eyes_iris_brown",
                eyebrowsItemId = "brow_curious",
                mouthItemId = "mouth_cat",
                blushItemId = "blush_peach",
                defaultClothesItemIds = listOf("top_sailor", "bot_denim_shorts", "shoes_sneakers"),
                defaultBackgroundId = "bg_sunshine"
            ),
            CharacterPreset(
                id = "preset_mimi",
                nameEn = "Mimi",
                nameVi = "Bé Mimi",
                descriptionEn = "Sweet pastel princess with cute bear bag",
                descriptionVi = "Công chúa ngọt ngào cùng túi gấu xinh",
                bodyItemId = "body_fair",
                faceItemId = "face_purple",
                hairItemId = "hair_lilac_twin",
                eyesItemId = "eyes_blue",
                eyesLineItemId = "eyes_line_blue",
                eyesIrisItemId = "eyes_iris_blue",
                eyebrowsItemId = "brow_confident",
                mouthItemId = "mouth_shy",
                blushItemId = "blush_pink",
                defaultClothesItemIds = listOf("dress_mint", "acc_bear_bag", "hat_cat_ears"),
                defaultBackgroundId = "bg_sakura"
            )
        )
    }
}
