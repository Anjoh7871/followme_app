package com.example.followme02.screen.settings

import android.app.Activity
import android.preference.PreferenceManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.followme02.R

@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    language: String,
    onToggleLanguage: (String) -> Unit,
    onUpdateUsername: (String) -> Unit,
    onDeleteUser: () -> Unit,
    onLogout: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity

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
                text = stringResource(R.string.dark_mode),
                checked = isDarkMode,
                onCheckedChange = { onToggleDarkMode() }
            )
        }

        // ---------------- LANGUAGE ----------------
        item {
            SectionTitle(stringResource(R.string.language))
        }

        item {
            SettingsItem(text = if (language == "NO") "English" else "Norsk") {

                val newLang = if (language == "NO") "EN" else "NO"
                onToggleLanguage(newLang)
                PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString("lang", newLang)
                    .apply()

                activity.recreate()


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
            SettingsItem(stringResource(R.string.logout), destructive = true) {
                onLogout()
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {  },

            title = {
                Text(stringResource(R.string.edit_username))
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
                        onUpdateUsername(newUsername)
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {  }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { },

            title = {
                Text(stringResource(R.string.delete))
            },

            text = {
                Text(stringResource(R.string.delete_question))
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteUser()
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
                    onClick = {  }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}