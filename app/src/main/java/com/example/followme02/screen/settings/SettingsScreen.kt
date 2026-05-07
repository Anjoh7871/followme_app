package com.example.followme02.screen.settings

import android.app.Activity
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.followme02.R
import com.example.followme02.screen.social.SearchResultCard
import com.example.followme02.screen.social.SocialFriendUi
import com.example.followme02.screen.social.SocialSearchCard
import com.example.followme02.screen.social.UserRow

@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    language: String,
    onToggleLanguage: (String) -> Unit,
    onUpdateUsername: (String) -> Unit,
    settingState: SettingsUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    blockedUsers: List<SocialFriendUi>,
    onLoadBlockedUsers: () -> Unit,
    onBlockUser: (Int) -> Unit,
    onUnblockUser: (Int) -> Unit,
    onDeleteUser: () -> Unit,
    onLogout: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as Activity
    var showBlockedDialog by remember { mutableStateOf(false) }

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
                showBlockedDialog = true
                onLoadBlockedUsers()
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
    if (showBlockedDialog) {

        Dialog(
            onDismissRequest = {
                showBlockedDialog = false
            }
        ) {

            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = stringResource(R.string.blocked_users),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // SEARCH
                        item {
                            SocialSearchCard(
                                title = stringResource(R.string.find_user_by_email),
                                value = settingState.searchQuery,
                                onValueChange = onSearchQueryChange,
                                placeholder = stringResource(R.string.example_email_com),
                                buttonText = stringResource(R.string.search),
                                onButtonClick = onSearchClick
                            )
                        }

                        // SEARCH RESULT
                        settingState.searchResult?.let { result ->

                            item {
                                SearchResultCard(
                                    result = result,
                                    actionLabel = stringResource(R.string.block),
                                    actionEnabled = true,
                                    onActionClick = {
                                        onBlockUser(result.userId)
                                        onLoadBlockedUsers()
                                    }
                                )
                            }
                        }

                        // BLOCKED LIST
                        items(blockedUsers) { user ->

                            UserRow(
                                user = user,
                                onClick = {},
                                showProfil = false,
                                clickable = false,

                                trailingContent = {

                                    TextButton(
                                        onClick = {
                                            onUnblockUser(user.userId)
                                        }
                                    ) {
                                        Text(stringResource(R.string.unblock))
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            showBlockedDialog = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}