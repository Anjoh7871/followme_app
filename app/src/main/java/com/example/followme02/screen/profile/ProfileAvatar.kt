package com.example.followme02.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.followme02.R

@Composable
fun ProfileAvatar(
    username: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF10B981), // grønn
            Color(0xFF3B82F6)  // blå
        )
    )

    val initials = username
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.first().uppercaseChar() }
        .take(2)
        .joinToString("")

    Box(
        modifier = modifier
            .size(82.dp)
            .clip(CircleShape)
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {

        if (!avatarUrl.isNullOrBlank()) {

            AsyncImage(
                model = avatarUrl,
                contentDescription = stringResource(R.string.profile_avatar_content_description),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        } else {

            Text(
                text = initials,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}