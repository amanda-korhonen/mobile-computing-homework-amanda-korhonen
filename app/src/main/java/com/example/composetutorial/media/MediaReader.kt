package com.example.composetutorial.media

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

class MediaReader (private val context: Context) {

    fun getAllMediaFiles(): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()
        val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
        )

        context.contentResolver.query(
            queryUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColum = cursor.getColumnIndexOrThrow(
                MediaStore.Files.FileColumns._ID
            )
            val nameColum = cursor.getColumnIndexOrThrow(
                MediaStore.Files.FileColumns.DISPLAY_NAME
            )
            val mimeTypeColum = cursor.getColumnIndexOrThrow(
                MediaStore.Files.FileColumns.MIME_TYPE
            )

            while (cursor.moveToNext()) {
                val id =cursor.getLong(idColum)
                val name = cursor.getString(nameColum)
                val mimeType = cursor.getString(mimeTypeColum)

                if(name != null && mimeType != null) {
                    val contentUri = ContentUris.withAppendedId(
                        queryUri,
                        id
                    )
                    val mediaType = when {
                        mimeType.startsWith("audio/") -> MediaType.AUDIO
                        mimeType.startsWith("video/") -> MediaType.VIDEO
                        else -> MediaType.IMAGE

                    }

                    mediaFiles.add (
                        MediaFile(
                            uri = contentUri,
                            name = name,
                            type = mediaType,
                        )
                    )
                }
            }
        }
        return mediaFiles.toList()
    }

}