package com.chibidoll.dressup.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SparkleParticle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun SparkleBurst(
    trigger: Long,
    modifier: Modifier = Modifier
) {
    if (trigger <= 0L) return

    val progress = remember(trigger) { Animatable(0f) }

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFFFF69B4), // Hot Pink
            Color(0xFFBA55D3), // Medium Orchid
            Color(0xFF7FFFD4), // Aquamarine
            Color(0xFFFFB6C1), // Light Pink
            Color(0xFFFFFACD)  // Lemon Chiffon
        )
        List(12) {
            val angle = (it * 30f + Random.nextFloat() * 15f) * (Math.PI.toFloat() / 180f)
            SparkleParticle(
                angle = angle,
                speed = Random.nextFloat() * 160f + 140f,
                color = colors[it % colors.size],
                size = Random.nextFloat() * 8f + 12f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 360f
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.45f)
            val p = progress.value
            val alpha = (1f - p).coerceIn(0f, 1f)

            particles.forEach { particle ->
                val distance = particle.speed * p
                val x = center.x + cos(particle.angle) * distance
                val y = center.y + sin(particle.angle) * distance
                val currentSize = particle.size * (1f - p * 0.5f)

                rotate(degrees = particle.rotationSpeed * p, pivot = Offset(x, y)) {
                    drawStar(
                        center = Offset(x, y),
                        size = currentSize,
                        color = particle.color.copy(alpha = alpha)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStar(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        // 4-point cute diamond star
        moveTo(center.x, center.y - size)
        quadraticBezierTo(center.x, center.y, center.x + size, center.y)
        quadraticBezierTo(center.x, center.y, center.x, center.y + size)
        quadraticBezierTo(center.x, center.y, center.x - size, center.y)
        quadraticBezierTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path = path, color = color)
}

