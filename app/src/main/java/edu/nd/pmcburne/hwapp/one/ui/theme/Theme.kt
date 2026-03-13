package edu.nd.pmcburne.hwapp.one.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    //primary = Purple80,
    //secondary = PurpleGrey80,
    //tertiary = Pink80
    primary = Color(0xFF8FB8FF),
    onPrimary = Color(0xFF002B6B),
    secondary = Color(0xFFFFC857),
    onSecondary = Color(0xFF3A2A00),
    tertiary = Color(0xFFFF8A80),
    background = Color(0xFF121417),
    onBackground = Color(0xFFE3E7ED),
    surface = Color(0xFF1A1D21),
    onSurface = Color(0xFFE3E7ED),
    surfaceVariant = Color(0xFF2A2F36),
    onSurfaceVariant = Color(0xFFC3C8D0)
)

private val LightColorScheme = lightColorScheme(
    //primary = Purple40,
    //secondary = PurpleGrey40,
    //tertiary = Pink40


    primary = Color(0xFF0B3D91),
    onPrimary = Color.White,
    secondary = Color(0xFFF5A623),
    onSecondary = Color(0xFF2D1B00),
    tertiary = Color(0xFFD64545),
    background = Color(0xFFF6F7FB),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE7EAF0),
    onSurfaceVariant = Color(0xFF5F6670)
)


@Composable
fun HWStarterRepoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}