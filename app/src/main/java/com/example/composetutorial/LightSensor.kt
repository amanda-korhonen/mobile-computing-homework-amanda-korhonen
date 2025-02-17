package com.example.composetutorial

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat

@Composable
fun LightSensor() {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    var currentLightLevel by remember { mutableFloatStateOf(0.0f) }

    val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                currentLightLevel = it.values[0]
                Log.d("LightSensor", "Current light level: $currentLightLevel")

                if (currentLightLevel > 10000.0f) {
                    sendLightNotification(
                        context, "Too bright!",
                        "You should go back indoors. Light > 10 000 lux"
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    DisposableEffect(Unit) {
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box (modifier = Modifier.fillMaxSize()) {
        Image(
            painter = getSunPainterResource(currentLightLevel),
            contentDescription = "Sun icon",
            modifier = Modifier.size(100.dp)
                .align(Alignment.TopCenter)
        )
    }

}

@Composable
fun getSunPainterResource(lightLevel: Float): Painter {
    return if (lightLevel > 10000.0f) {
        painterResource(id = R.drawable.baseline_wb_sunny_24_lights)
    } else {
        painterResource(id = R.drawable.twotone_wb_sunny_24)
    }

}

@SuppressLint("ServiceCast")
fun sendLightNotification(context: Context, title: String, content: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    // Create notification channel if not already created
    val channelId = "light_channel_id"
    val channel = NotificationChannel(
        channelId, "Light Notifications", NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Light level notification channel"
    }
    notificationManager.createNotificationChannel(channel)

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_notifications_24)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()
    notificationManager.notify(1, notification)
}

