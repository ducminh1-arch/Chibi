import SwiftUI

/// Animated particle burst effect triggered when an item is equipped.
public struct SparkleView: View {
    public let trigger: Int

    @State private var particles: [SparkleParticle] = []

    public init(trigger: Int) {
        self.trigger = trigger
    }

    public var body: some View {
        ZStack {
            ForEach(particles) { particle in
                Image(systemName: "sparkle")
                    .font(.system(size: particle.size))
                    .foregroundColor(particle.color)
                    .scaleEffect(particle.scale)
                    .opacity(particle.opacity)
                    .offset(x: particle.x, y: particle.y)
            }
        }
        .allowsHitTesting(false)
        .onChange(of: trigger) { _, _ in
            burst()
        }
    }

    private func burst() {
        let colors: [Color] = [.yellow, .pink, .purple, .mint, .white]
        var newParticles: [SparkleParticle] = []

        for _ in 0..<12 {
            let angle = Double.random(in: 0...(2 * .pi))
            let distance = CGFloat.random(in: 30...90)
            let particle = SparkleParticle(
                x: cos(angle) * distance,
                y: sin(angle) * distance,
                color: colors.randomElement() ?? .yellow,
                size: CGFloat.random(in: 12...22)
            )
            newParticles.append(particle)
        }

        particles = newParticles

        withAnimation(.easeOut(duration: 0.6)) {
            for i in particles.indices {
                particles[i].scale = 1.3
                particles[i].opacity = 0.0
                particles[i].x *= 1.4
                particles[i].y *= 1.4
            }
        }

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.65) {
            particles.removeAll()
        }
    }
}

private struct SparkleParticle: Identifiable {
    let id = UUID()
    var x: CGFloat
    var y: CGFloat
    var color: Color
    var size: CGFloat
    var scale: CGFloat = 0.2
    var opacity: Double = 1.0
}

