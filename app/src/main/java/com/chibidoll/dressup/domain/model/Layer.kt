package com.chibidoll.dressup.domain.model

import kotlinx.serialization.Serializable

enum class Layer(val zIndex: Int, val key: String) {
    BACKGROUND(0, "background"),
    WINGS(5, "wings"),
    HAIR_BACK(10, "hair_back"),
    TAIL(15, "tail"),
    BODY(20, "body"),
    SOCKS(25, "socks"),
    SHOES(30, "shoes"),
    BOTTOM(40, "bottom"),
    TOP(50, "top"),
    DRESS(60, "dress"),
    OUTERWEAR(70, "outerwear"),
    BLUSH(72, "blush"),
    MOUTH(75, "mouth"),
    FACE(78, "face"),
    EYES_IRIS(79, "eyes_iris"),
    EYES(80, "eyes"),
    EYES_LINE(82, "eyes_line"),
    EYEBROWS(85, "eyebrows"),
    GLASSES(88, "glasses"),
    EARS(92, "ears"),
    HAIR_FRONT(95, "hair_front"),
    HAT(100, "hat"),
    ACCESSORY(110, "accessory");

    companion object {
        fun fromKey(key: String): Layer = when (key.lowercase()) {
            "background" -> BACKGROUND
            "wings" -> WINGS
            "hair_back" -> HAIR_BACK
            "tail" -> TAIL
            "body" -> BODY
            "socks" -> SOCKS
            "shoes" -> SHOES
            "bottom" -> BOTTOM
            "top" -> TOP
            "dress" -> DRESS
            "outerwear" -> OUTERWEAR
            "blush" -> BLUSH
            "mouth" -> MOUTH
            "face" -> FACE
            "eyes_iris", "iris" -> EYES_IRIS
            "eyes_line", "line" -> EYES_LINE
            "eyes" -> EYES
            "eyebrows", "brows", "brow" -> EYEBROWS
            "glasses" -> GLASSES
            "ears" -> EARS
            "hair_front" -> HAIR_FRONT
            "hair" -> HAIR_FRONT
            "hat" -> HAT
            "accessory" -> ACCESSORY
            else -> ACCESSORY
        }
    }
}


