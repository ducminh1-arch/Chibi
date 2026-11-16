import SwiftUI

/// Two-level category tab bar:
/// - Top row: Groups (Doll, Clothes, Accessories, Scene)
/// - Bottom row: Subcategories of the selected group
public struct CategoryTabsView: View {
    public let groups: [CategoryGroup]
    public let selectedGroup: CategoryGroup
    public let selectedSubcategory: CategorySubcategory
    public let onGroupSelected: (CategoryGroup) -> Void
    public let onSubcategorySelected: (CategorySubcategory) -> Void

    public init(
        groups: [CategoryGroup],
        selectedGroup: CategoryGroup,
        selectedSubcategory: CategorySubcategory,
        onGroupSelected: @escaping (CategoryGroup) -> Void,
        onSubcategorySelected: @escaping (CategorySubcategory) -> Void
    ) {
        self.groups = groups
        self.selectedGroup = selectedGroup
        self.selectedSubcategory = selectedSubcategory
        self.onGroupSelected = onGroupSelected
        self.onSubcategorySelected = onSubcategorySelected
    }

    public var body: some View {
        VStack(spacing: 8) {
            // Level 1: Category Groups
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 12) {
                    ForEach(groups) { group in
                        let isSelected = (group.id == selectedGroup.id)
                        Button(action: {
                            withAnimation(.spring(response: 0.3, dampingFraction: 0.7)) {
                                onGroupSelected(group)
                            }
                        }) {
                            HStack(spacing: 6) {
                                Image(systemName: group.systemImage)
                                    .font(.system(size: 15, weight: .semibold))
                                Text(group.displayName())
                                    .font(.system(size: 14, weight: .bold))
                            }
                            .foregroundColor(isSelected ? .white : Color(red: 0.4, green: 0.3, blue: 0.5))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(
                                Capsule()
                                    .fill(isSelected
                                          ? LinearGradient(colors: [Color(red: 1.0, green: 0.55, blue: 0.75), Color(red: 1.0, green: 0.45, blue: 0.65)], startPoint: .topLeading, endPoint: .bottomTrailing)
                                          : LinearGradient(colors: [Color.white.opacity(0.8), Color.white.opacity(0.6)], startPoint: .top, endPoint: .bottom)
                                    )
                                    .shadow(color: isSelected ? Color.pink.opacity(0.3) : Color.black.opacity(0.05), radius: 3, x: 0, y: 2)
                            )
                        }
                    }
                }
                .padding(.horizontal, 16)
            }

            // Level 2: Subcategories
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(selectedGroup.subcategories) { subcat in
                        let isSelected = (subcat.id == selectedSubcategory.id)
                        Button(action: {
                            withAnimation(.spring(response: 0.3, dampingFraction: 0.7)) {
                                onSubcategorySelected(subcat)
                            }
                        }) {
                            VStack(spacing: 3) {
                                Image(systemName: subcat.systemImage)
                                    .font(.system(size: 18, weight: .medium))
                                Text(subcat.displayName())
                                    .font(.system(size: 11, weight: .medium))
                                    .lineLimit(1)
                            }
                            .frame(minWidth: 54, minHeight: 48)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .foregroundColor(isSelected ? Color(red: 0.85, green: 0.25, blue: 0.55) : Color(red: 0.45, green: 0.4, blue: 0.5))
                            .background(
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(isSelected ? Color.pink.opacity(0.18) : Color.white.opacity(0.6))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isSelected ? Color.pink : Color.clear, lineWidth: 2)
                                    )
                            )
                        }
                    }
                }
                .padding(.horizontal, 16)
            }
        }
        .padding(.vertical, 4)
    }
}

