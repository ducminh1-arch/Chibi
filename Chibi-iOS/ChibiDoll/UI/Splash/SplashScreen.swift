import SwiftUI

/// Animated splash screen replicating Android `SplashScreen.kt` with a 1.5s gentle pulse transition.
public struct SplashScreen: View {
    public let onFinished: () -> Void

    @State private var isPulsing: Bool = false

    public init(onFinished: @escaping () -> Void) {
        self.onFinished = onFinished
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color(red: 1.0, green: 0.94, blue: 0.97), Color(red: 0.97, green: 0.92, blue: 0.99), Color(red: 0.93, green: 0.91, blue: 0.98)],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 20) {
                // Cute Logo Placeholder with Pulsing Scale
                ZStack {
                    Circle()
                        .fill(
                            LinearGradient(
                                colors: [Color(red: 1.0, green: 0.70, blue: 0.78), Color(red: 0.78, green: 0.72, blue: 0.92)],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .frame(width: 130, height: 130)
                        .shadow(color: Color.pink.opacity(0.3), radius: 16, x: 0, y: 8)

                    Image(systemName: "sparkles")
                        .font(.system(size: 54, weight: .bold))
                        .foregroundColor(.white)
                }
                .scaleEffect(isPulsing ? 1.06 : 0.94)

                VStack(spacing: 6) {
                    Text(String(localized: "app_name", defaultValue: "Chibi Doll"))
                        .font(.system(size: 32, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                    Text(String(localized: "tagline", defaultValue: "Cute Paper Doll Dress Up"))
                        .font(.system(size: 14, weight: .medium, design: .rounded))
                        .foregroundColor(Color(red: 0.6, green: 0.5, blue: 0.65))
                }
            }
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 0.8).repeatForever(autoreverses: true)) {
                isPulsing = true
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                onFinished()
            }
        }
    }
}

