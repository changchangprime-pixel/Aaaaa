package com.example.suhaengpyeong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.suhaengpyeong.navigation.AppNavGraph
import com.example.suhaengpyeong.ui.theme.SuhaengpyeongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuhaengpyeongTheme {
                AppNavGraph()
            }
        }
    }
}
