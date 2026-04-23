package com.example.followme02.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ---------------- EDIT PROFILE ----------------
        item {
            SectionTitle("Edit profile")
        }

        item {
            SettingsItem("Edit username") {
                showEditDialog = true
            }
        }

        // ---------------- APPEARANCE ----------------
        item {
            SectionTitle("Appearance")
        }

        item {
            SettingsToggle(
                text = "Dark mode",
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode() }
            )
        }

        // ---------------- LANGUAGE ----------------
        item {
            SectionTitle("Language")
        }

        item {
            SettingsItem("${state.language}") {
                viewModel.setLanguage(
                    if (state.language == "NO") "EN" else "NO"
                )
            }
        }

        // ---------------- BLOCKED USERS ----------------
        item {
            SectionTitle("Blocked users")
        }

        item {
            SettingsItem("View blocked users") {
                navController.navigate("blockedUsers")
            }
        }

        // ---------------- ACCOUNT ----------------
        item {
            SectionTitle("Account")
        }

        item {
            SettingsItem("Delete user", destructive = true) {
                showDeleteDialog = true
            }
        }

        item {
            SettingsItem("Log out", destructive = true) {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },

            title = {
                Text("Edit username")
            },

            text = {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("New username") }
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateUsername(newUsername)
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },

            title = {
                Text("Delete account")
            },

            text = {
                Text("Are you sure you want to delete your account? This action cannot be undone.")
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        showDeleteDialog = false

                        viewModel.deleteUser()

                        navController.navigate("login") {

                            popUpTo(0)

                        }

                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}