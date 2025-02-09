package com.example.composetutorial

import SampleData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    ) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "mainScreen"
    ) {
        composable("mainScreen") {
            Conversation(
                onNavigateToSettings = { navController.navigate("settings") },
                SampleData.conversationSample
            )
        }
        composable("settings") {
            Settings(
                onNavigateBack = {navController.popBackStack("mainScreen", false)}
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ComposeTutorialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    MyAppNavHost(navController = navController)
                }
            }
        }
    }
}

