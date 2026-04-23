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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.followme02.R
import com.example.followme02.viewmodel.SettingsViewModel
import com.example.followme02.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsviewModel: SettingsViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    val state by settingsviewModel.uiState.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode
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
            SectionTitle(stringResource(R.string.edit_profile))
        }

        item {
            SettingsItem(stringResource(R.string.edit_username)) {
                showEditDialog = true
            }
        }

        // ---------------- APPEARANCE ----------------
        item {
            SectionTitle(stringResource(R.string.appearance))
        }

        item {
            SettingsToggle(
                stringResource(R.string.dark_mode),
                isDarkMode,
                { themeViewModel.toggleDarkMode() }
            )
        }

        // ---------------- LANGUAGE ----------------
        item {
            SectionTitle(stringResource(R.string.language))
        }

        item {
            SettingsItem("${state.language}") {
                settingsviewModel.setLanguage(
                    if (state.language == "NO") "EN" else "NO"
                )
            }
        }

        // ---------------- BLOCKED USERS ----------------
        item {
            SectionTitle(stringResource(R.string.blocked_users))
        }

        item {
            SettingsItem(stringResource(R.string.view_blocked_users)) {
                navController.navigate("blockedUsers")
            }
        }

        // ---------------- ACCOUNT ----------------
        item {
            SectionTitle(stringResource(R.string.account))
        }

        item {
            SettingsItem(stringResource(R.string.delete_user), destructive = true) {

                showDeleteDialog = true
            }
        }

        item {
            SettingsItem(stringResource(R.string.log_out), destructive = true) {
                settingsviewModel.logout()
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
                    label = { Text(stringResource(R.string.new_username)) }
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        settingsviewModel.updateUsername(newUsername)
                        showEditDialog = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },

            title = {
                Text(stringResource(R.string.delete_user))
            },

            text = {
                Text(stringResource(R.string.delete_question))
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        showDeleteDialog = false

                        settingsviewModel.deleteUser()

                        navController.navigate("login") {

                            popUpTo(0)

                        }

                    }
                ) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}