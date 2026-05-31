package com.example.assetstar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.assetstar.R

@Composable
fun LottieAssetStarEffect(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.asset_star_core))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.graphicsLayer(alpha = alpha),
    )
}

@Composable
fun LottieAssetPlanetEffect(
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.asset_planet_node))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.graphicsLayer(alpha = alpha),
    )
}
