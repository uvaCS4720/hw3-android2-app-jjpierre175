package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.nd.pmcburne.hwapp.one.ScoresScreen
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { // ScoreScreen is the main screen
            HWStarterRepoTheme {
                ScoresScreen()
            }
        }
    }
}