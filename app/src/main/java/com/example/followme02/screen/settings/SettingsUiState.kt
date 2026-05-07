package com.example.followme02.screen.settings

import com.example.followme02.screen.social.SocialFriendUi
import com.example.followme02.screen.social.SocialUserSearchResultUi


data class SettingsUiState(

    // Language
    val language: String = "EN",

    // Blocked users
    val searchQuery: String = "",
    val searchResult: SocialUserSearchResultUi? = null,
    val searchMessage: String? = null,
    val blockedUsers: List<SocialFriendUi> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)