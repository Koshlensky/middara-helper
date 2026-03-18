package com.middara.helper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.middara.helper.ui.navigation.NavGraph
import com.middara.helper.ui.theme.MiddaraHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiddaraHelperTheme {
                NavGraph()
            }
        }
    }
}
