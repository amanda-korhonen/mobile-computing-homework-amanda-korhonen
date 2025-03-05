package com.example.composetutorial

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.composetutorial.data.UserViewModel
import com.example.composetutorial.data.UserViewModelFactory
import com.example.composetutorial.media.MediaFile
import com.example.composetutorial.media.MediaReader
import com.example.composetutorial.media.MediaType

@Composable
fun MediaDisplayScreen(onNavigateBack: () -> Unit,
                       onRequestPermissions: () -> Unit) {
    val context = LocalContext.current
    val  mediaReader by lazy {
        MediaReader(context) }

    val userViewModel: UserViewModel = viewModel(
        factory =  UserViewModelFactory(
            context.applicationContext as Application,
            mediaReader = mediaReader
        )
    )
    Scaffold { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.TopCenter) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                items(userViewModel.files) {
                    MediaListItem(
                        file = it,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            ) {
                Text("Request Permissions")
            }
            Text(text = "Media",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 20.sp,
                modifier = Modifier.padding(paddingValues)
            )

        }
    }
}


@Composable
fun MediaListItem(
    file: MediaFile,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier.padding(top = 72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when(file.type) {
            MediaType.IMAGE -> {
                AsyncImage(
                    model = file.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                )
            }
            MediaType.VIDEO -> {
                Image(
                    painter = painterResource(id = R.drawable.video),
                    contentDescription = null,
                    modifier = Modifier.width(100.dp)
                )
            }
            MediaType.AUDIO -> {
                Image(
                    painter = painterResource(id = R.drawable.audio),
                    contentDescription = null,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
        Text(text = "${file.name} - ${file.type}",
            modifier = Modifier.padding(16.dp).weight(1f))
    }
}
