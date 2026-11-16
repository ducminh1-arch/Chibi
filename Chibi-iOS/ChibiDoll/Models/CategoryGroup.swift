import Foundation

/// Two-level navigation category group replicating Android `CategoryGroup.kt`.
public struct CategoryGroup: Identifiable, Hashable, Sendable {
    public let id: String
    public let nameEn: String
    public let nameVi: String
    public let systemImage: String
    public let subcategories: [CategorySubcategory]

    public init(id: String, nameEn: String, nameVi: String, systemImage: String, subcategories: [CategorySubcategory]) {
        self.id = id
        self.nameEn = nameEn
        self.nameVi = nameVi
        self.systemImage = systemImage
        self.subcategories = subcategories
    }

    public func displayName(isVietnamese: Bool = false) -> String {
        isVietnamese && !nameVi.isEmpty ? nameVi : nameEn
    }

    public static func defaultGroups() -> [CategoryGroup] {
        [
            CategoryGroup(
                id = "appearance",
                nameEn: "Doll",
                nameVi: "Nhân vật",
                systemImage: "face.smiling",
                subcategories: [
                    CategorySubcategory(id: "hair", nameEn: "Hairstyle", nameVi: "Kiểu tóc", systemImage: "person.crop.circle"),
                    CategorySubcategory(id: "eyes_line", nameEn: "Eye Shape", nameVi: "Dáng mắt", systemImage: "eye"),
                    CategorySubcategory(id: "eyes_iris", nameEn: "Eye Color", nameVi: "Màu mắt", systemImage: "eye.fill"),
                    CategorySubcategory(id: "eyebrows", nameEn: "Eyebrows", nameVi: "Lông mày", systemImage: "waveform.path"),
                    CategorySubcategory(id: "mouth", nameEn: "Mouth", nameVi: "Khuôn miệng", systemImage: "mouth.fill"),
                    CategorySubcategory(id: "blush", nameEn: "Blush", nameVi: "Má hồng", systemImage: "heart.fill"),
                    CategorySubcategory(id: "body", nameEn: "Skin & Body", nameVi: "Cơ thể & Da", systemImage: "figure.stand")
                ]
            ),
            CategoryGroup(
                id = "clothes",
                nameEn: "Clothes",
                nameVi: "Trang phục",
                systemImage: "hanger",
                subcategories: [
                    CategorySubcategory(id: "dress", nameEn: "Dresses", nameVi: "Váy liền", systemImage: "tshirt.fill"),
                    CategorySubcategory(id: "top", nameEn: "Tops", nameVi: "Áo", systemImage: "hanger"),
                    CategorySubcategory(id: "bottom", nameEn: "Bottoms", nameVi: "Quần & Váy", systemImage: "bag.fill"),
                    CategorySubcategory(id: "outerwear", nameEn: "Jackets", nameVi: "Áo khoác", systemImage: "square.stack.3d.down.right.fill"),
                    CategorySubcategory(id: "socks", nameEn: "Socks", nameVi: "Tất & Vớ", systemImage: "shoeprints.fill"),
                    CategorySubcategory(id: "shoes", nameEn: "Shoes", nameVi: "Giày", systemImage: "figure.walk")
                ]
            ),
            CategoryGroup(
                id = "accessories",
                nameEn: "Accessories",
                nameVi: "Phụ kiện",
                systemImage: "sparkles",
                subcategories: [
                    CategorySubcategory(id: "hat", nameEn: "Hats & Bows", nameVi: "Mũ & Nơ", systemImage: "crown.fill"),
                    CategorySubcategory(id: "glasses", nameEn: "Glasses", nameVi: "Mắt kính", systemImage: "eyeglasses"),
                    CategorySubcategory(id: "ears", nameEn: "Ears", nameVi: "Tai thú & Elf", systemImage: "hare.fill"),
                    CategorySubcategory(id: "tail", nameEn: "Tails", nameVi: "Đuôi thú", systemImage: "pawprint.fill"),
                    CategorySubcategory(id: "wings", nameEn: "Wings", nameVi: "Đôi cánh", systemImage: "wind"),
                    CategorySubcategory(id: "accessory", nameEn: "Handheld & Deco", nameVi: "Phụ kiện", systemImage: "sparkles")
                ]
            ),
            CategoryGroup(
                id = "scene",
                nameEn: "Scene",
                nameVi: "Khung cảnh",
                systemImage: "photo.fill",
                subcategories: [
                    CategorySubcategory(id: "background", nameEn: "Wallpaper", nameVi: "Hình nền", systemImage: "photo.on.rectangle")
                ]
            )
        ]
    }
}

public struct CategorySubcategory: Identifiable, Hashable, Sendable {
    public let id: String
    public let nameEn: String
    public let nameVi: String
    public let systemImage: String

    public init(id: String, nameEn: String, nameVi: String, systemImage: String) {
        self.id = id
        self.nameEn = nameEn
        self.nameVi = nameVi
        self.systemImage = systemImage
    }

    public func displayName(isVietnamese: Bool = false) -> String {
        isVietnamese && !nameVi.isEmpty ? nameVi : nameEn
    }
}

