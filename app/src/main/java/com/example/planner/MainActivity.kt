package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.planner.ui.theme.PlannerTheme

// Why: the single activity. All screens are composables; rotation recreates it (no configChanges).
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlannerTheme {
                PlannerApp()
            }
        }
    }
}
