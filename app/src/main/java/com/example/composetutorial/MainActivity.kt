package com.example.composetutorial

import SampleData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

@Composable
fun MainScreen(notificationHelper: NotificationHelper) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult =  {isEnabled ->
            if (isEnabled) {
                notificationHelper.createNotification("Test notification",
                    "This is just a test.")
            }
        }
    )
    Column {
        Spacer(modifier = Modifier.padding((10.dp)))
        Row(modifier = Modifier.padding(20.dp)) {
            ExtendedFloatingActionButton(
                onClick = { notificationHelper.requestPermission(launcher)},
                modifier =  Modifier.width(200.dp).height(60.dp)
            ) {
                Text(text = "Enable Notifications")
            }
        }
        //Tähän sitte kutsutaan sitä sensoriluokkaa slay
    }

}

