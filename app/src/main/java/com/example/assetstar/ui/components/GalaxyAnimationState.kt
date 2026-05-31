package com.example.assetstar.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

data class GalaxyAnimationState(
    val globalRotation: Float,
    val satelliteAngle: Float,
    val starPulse: Float,
    val starFlowAngle: Float,
    val textureFlow: Float,
    val orbitFlow: Float,
    val starTwinkle: Float,
    val nodeFloat: Float,
    val bottomGlow: Float,
)

@Composable
fun rememberGalaxyAnimationState(
    enableGalaxyAnimation: Boolean = true,
): GalaxyAnimationState {
    if (!enableGalaxyAnimation) {
        return GalaxyAnimationState(
            globalRotation = 0f,
            satelliteAngle = 0f,
            starPulse = 0.62f,
            starFlowAngle = 0f,
            textureFlow = 0f,
            orbitFlow = 0f,
            starTwinkle = 0f,
            nodeFloat = 0f,
            bottomGlow = 1f,
        )
    }

    val transition = rememberInfiniteTransition(label = "galaxyAnimation")
    val globalRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 150_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "globalRotation",
    )
    val satelliteAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "satelliteAngle",
    )
    val starPulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "starPulse",
    )
    val starFlowAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "starFlowAngle",
    )
    val textureFlow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 36_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "textureFlow",
    )
    val orbitFlow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 26_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orbitFlow",
    )
    val starTwinkle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "starTwinkle",
    )
    val nodeFloat by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "nodeFloat",
    )
    val bottomGlow by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bottomGlow",
    )

    return GalaxyAnimationState(
        globalRotation = globalRotation,
        satelliteAngle = satelliteAngle,
        starPulse = starPulse,
        starFlowAngle = starFlowAngle,
        textureFlow = textureFlow,
        orbitFlow = orbitFlow,
        starTwinkle = starTwinkle,
        nodeFloat = nodeFloat,
        bottomGlow = bottomGlow,
    )
}
