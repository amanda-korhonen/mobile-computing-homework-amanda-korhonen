package com.example.composetutorial

import android.app.Application
import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.composetutorial.data.User
import com.example.composetutorial.data.UserViewModel
import com.example.composetutorial.data.UserViewModelFactory
import com.example.composetutorial.media.MediaReader
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import java.io.File

@Composable
fun Conversation(onNavigateToSettings: () -> Unit,
                 onNavigateToMedia: () -> Unit,
                 messages: List<Message>) {
    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding((paddingValues))) {
            LazyColumn (modifier = Modifier.padding(top= 72.dp)) {
                items(messages) { message ->
                    MessageCard(message)
                }
            }
            //Settings button
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ){
                Icon(
                    Icons.Rounded.Settings,
                    contentDescription = "Settings button",
                )
            }
            //Media button
            Button(
                onClick = onNavigateToMedia,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ){
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = "Media button",
                )
            }
        }
    }
}

data class Message(val author: String, val body: String)

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        val context = LocalContext.current
        var profilePhoto = File(context.filesDir, "profile.jpg")
        val painter = rememberAsyncImagePainter(profilePhoto)
        val mediaReader = MediaReader(context)

        val userViewModel: UserViewModel = viewModel(
            factory =  UserViewModelFactory(
                context.applicationContext as Application,
                mediaReader = mediaReader
            )
        )
        userViewModel.insertUser(User(1,"User"))

        val user by userViewModel.userData.collectAsState(initial = null)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)

        )
        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this
        // variable
        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )

        // We toggle the isExpanded variable when we click on this Column
        Column (modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = user?.userName ?: "", //userName

                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier.animateContentSize().padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/*@Preview
@Composable
fun PreviewConversation() {
    ComposeTutorialTheme {
        Conversation(
            onNavigateToSettings = {},
            com.example.composetutorial.SampleData.conversationSample)
    }
}*/

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Preview
@Composable
fun PreviewMessageCard() {
    ComposeTutorialTheme {
        Surface {
            MessageCard(
                msg = Message("Lexi", "Take a look at Jetpack Compose, it's great!")
            )
        }
    }
}
