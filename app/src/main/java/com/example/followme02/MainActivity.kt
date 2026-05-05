package com.example.followme02

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import com.example.followme02.ui.theme.FollowMe02Theme
import com.example.followme02.viewmodel.ThemeViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode

                FollowMe02Theme(
                    darkTheme = isDarkMode,
                    dynamicColor = false
                ) {
                    FollowMeApp(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = themeViewModel::toggleDarkMode,
                        onLoadDarkMode = themeViewModel::loadDarkMode,
                        onResetDarkMode = themeViewModel::resetToLightMode
                    )
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(newBase)
        val lang = prefs.getString("lang", "EN") ?: "EN"

        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FollowMe02Theme(darkTheme = false) {
    }
}