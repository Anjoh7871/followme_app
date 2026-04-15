package com.example.followme02.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HeaderNavigation(navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        NavButton("Home") {
            navController.navigate("home")
        }

        NavButton("Social") {
            navController.navigate("social")
        }

        NavButton("Achievements") {
            navController.navigate("achievements")
        }

        NavButton("Profile") {
            navController.navigate("profile")
        }
    }
}

@Composable
fun NavButton(title: String, onClick: () -> Unit) {

    TextButton(
        onClick = onClick
    ) {
        Text(
            text = title,
            fontSize = 16.sp
        )
    }
}