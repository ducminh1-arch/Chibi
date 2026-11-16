import SwiftUI

/// Floating action button columns on the sides of the character canvas matching reference UI.
public struct LeftSideButtonColumn: View {
    public let onBack: () -> Void
    public let onReset: () -> Void
    public let onRandomize: () -> Void

    public init(onBack: @escaping () -> Void, onReset: @escaping () -> Void, onRandomize: @escaping () -> Void) {
        self.onBack = onBack
        self.onReset = onReset
        self.onRandomize = onRandomize
    }

    public var body: some View {
        VStack(spacing: 12) {
            CircleActionButton(icon: "chevron.backward", color: .purple, action: onBack)
            CircleActionButton(icon: "arrow.counterclockwise", color: .pink, action: onReset)
            CircleActionButton(icon: "dice.fill", color: .mint, action: onRandomize)
        }
    }
}

public struct RightSideButtonColumn: View {
    public let onConfirm: () -> Void
    public let onPaletteToggle: () -> Void
    public let onBackgroundPicker: () -> Void

    public init(onConfirm: @escaping () -> Void, onPaletteToggle: @escaping () -> Void, onBackgroundPicker: @escaping () -> Void) {
        self.onConfirm = onConfirm
        self.onPaletteToggle = onPaletteToggle
        self.onBackgroundPicker = onBackgroundPicker
    }

    public var body: some View {
        VStack(spacing: 12) {
            CircleActionButton(icon: "checkmark", color: .green, action: onConfirm)
            CircleActionButton(icon: "paintpalette.fill", color: .orange, action: onPaletteToggle)
            CircleActionButton(icon: "photo.fill", color: .blue, action: onBackgroundPicker)
        }
    }
}

private struct CircleActionButton: View {
    let icon: String
    let color: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: icon)
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(color)
                .frame(width: 42, height: 42)
                .background(
                    Circle()
                        .fill(Color.white.opacity(0.92))
                        .shadow(color: Color.black.opacity(0.12), radius: 3, x: 0, y: 2)
                )
        }
        .scaleEffect(1.0)
    }
}

