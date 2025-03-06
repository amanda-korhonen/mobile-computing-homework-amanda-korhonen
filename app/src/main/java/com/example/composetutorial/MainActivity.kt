package com.example.composetutorial

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetutorial.data.UserViewModel
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import com.example.composetutorial.data.UserViewModelFactory
import com.example.composetutorial.media.MediaReader
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    notificationHelper: NotificationHelper,
    onRequestPermissions: () -> Unit,
    snackBarHostState: SnackbarHostState,
    userViewModel: UserViewModel

    ) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "mainScreen"
    ) {
        composable("mainScreen") {
            Conversation(
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToMedia = {navController.navigate("mediaDisplay")},
                SampleData.conversationSample
            )
        }
        composable("settings") {
            Settings(
                onNavigateBack = {navController.popBackStack("mainScreen",
                    false)}, notificationHelper = notificationHelper
            )
        }
        composable("mediaDisplay") {
            MediaDisplayScreen(
                onNavigateBack = {navController.popBackStack("mainScreen",
                    false)},
                onRequestPermissions = onRequestPermissions,
                snackBarHostState = snackBarHostState,
                userViewModel = userViewModel
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var viewModel: UserViewModel
    private lateinit var multiplePermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private val permissionsToRequest  = arrayOf(
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_IMAGES,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize MediaReader
        val mediaReader = MediaReader(applicationContext)
        // Initialize UserViewModel with custom factory
        viewModel = ViewModelProvider(this, UserViewModelFactory(application, mediaReader)
        )[UserViewModel::class.java]

        val snackBarHostState = SnackbarHostState()

        //Splash Screen
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
            setOnExitAnimationListener {screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.6f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.6f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }
                zoomX.start()
                zoomY.start()
            }
        }

        enableEdgeToEdge()

        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        multiplePermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            permissionsToRequest.forEach { permission ->
                val isGranted = perms[permission] == true
                viewModel.onPermissionResult(permission, isGranted)
                if (!isGranted) {
                    viewModel.showSnackbarMessage(viewModel.getPermissionDeniedMessage())
                }
            }
        }

        setContent {
            ComposeTutorialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    MyAppNavHost(
                        navController = navController,
                        notificationHelper = notificationHelper,
                        onRequestPermissions = {requestPermissions()},
                        snackBarHostState = snackBarHostState,
                        userViewModel = viewModel
                    )
                }
            }
        }
    }
    private fun requestPermissions() {
        multiplePermissionResultLauncher.launch(permissionsToRequest)
    }
}
