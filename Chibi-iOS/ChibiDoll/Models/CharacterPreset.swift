import Foundation

/// Preset character models fixing body, face, and hair.
/// 1:1 match with Android `CharacterPreset`.
public struct CharacterPreset: Identifiable, Codable, Hashable, Sendable {
    public let id: String
    public let nameEn: String
    public let nameVi: String
    public let descriptionEn: String
    public let descriptionVi: String
    public let bodyItemId: String
    public let faceItemId: String
    public let hairItemId: String
    public let eyesItemId: String
    public let eyesLineItemId: String
    public let eyesIrisItemId: String
    public let eyebrowsItemId: String
    public let mouthItemId: String
    public let blushItemId: String?
    public let defaultClothesItemIds: [String]
    public let defaultBackgroundId: String?

    public init(
        id: String,
        nameEn: String,
        nameVi: String,
        descriptionEn: String,
        descriptionVi: String,
        bodyItemId: String = "body_fair",
        faceItemId: String = "face_purple",
        hairItemId: String,
        eyesItemId: String = "eyes_purple",
        eyesLineItemId: String = "eyes_line_purple",
        eyesIrisItemId: String = "eyes_iris_purple",
        eyebrowsItemId: String = "brow_gentle",
        mouthItemId: String = "mouth_smile",
        blushItemId: String? = nil,
        defaultClothesItemIds: [String] = [],
        defaultBackgroundId: String? = "bg_pastel_dream"
    ) {
        self.id = id
        self.nameEn = nameEn
        self.nameVi = nameVi
        self.descriptionEn = descriptionEn
        self.descriptionVi = descriptionVi
        self.bodyItemId = bodyItemId
        self.faceItemId = faceItemId
        self.hairItemId = hairItemId
        self.eyesItemId = eyesItemId
        self.eyesLineItemId = eyesLineItemId
        self.eyesIrisItemId = eyesIrisItemId
        self.eyebrowsItemId = eyebrowsItemId
        self.mouthItemId = mouthItemId
        self.blushItemId = blushItemId
        self.defaultClothesItemIds = defaultClothesItemIds
        self.defaultBackgroundId = defaultBackgroundId
    }

    public func displayName(isVietnamese: Bool = false) -> String {
        isVietnamese && !nameVi.isEmpty ? nameVi : nameEn
    }

    public func displayDescription(isVietnamese: Bool = false) -> String {
        isVietnamese && !descriptionVi.isEmpty ? descriptionVi : descriptionEn
    }

    public static func defaultPresets() -> [CharacterPreset] {
        [
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
                defaultClothesItemIds = ["top_lavender_sweater", "bottom_navy_skirt", "shoes_mary_jane"],
                defaultBackgroundId = "bg_pastel_dream"
            ),
            CharacterPreset(
                id = "preset_sakura",
                nameEn = "Sakura",
                nameVi = "Bé Sakura",
                descriptionEn = "Playful pink waves & charming winking smile",
                descriptionVi = "Tóc hồng đáng yêu và nụ cười nháy mắt",
                bodyItemId = "body_fair",
                faceItemId = "face_wink",
                hairItemId = "hair_pink",
                eyesItemId = "eyes_wink_pink",
                eyesLineItemId = "eyes_line_wink",
                eyesIrisItemId = "eyes_iris_pink",
                eyebrowsItemId = "brow_playful",
                mouthItemId = "mouth_happy_open",
                blushItemId = "blush_hearts",
                defaultClothesItemIds = ["dress_princess_pink", "shoes_pink_sneakers", "hat_giant_bow"],
                defaultBackgroundId = "bg_sweet_hearts"
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
                defaultClothesItemIds = ["top_sailor", "bottom_denim_shorts", "shoes_pink_sneakers"],
                defaultBackgroundId = "bg_sunny_sunshine"
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
                defaultClothesItemIds = ["dress_mint_polka", "acc_bear_bag", "hat_cat_ears"],
                defaultBackgroundId = "bg_cherry_blossom"
            )
        ]
    }
}

