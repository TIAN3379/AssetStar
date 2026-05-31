package com.example.assetstar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.assetstar.ui.navigation.AppNavGraph
import com.example.assetstar.ui.theme.AssetStarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssetStarTheme {
                AppNavGraph()
            }
        }
    }
}
