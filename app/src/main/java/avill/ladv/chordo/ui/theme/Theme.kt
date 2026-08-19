package avill.ladv.chordo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EmeraldLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = EmeraldSecondary,
    onSecondary = EmeraldOnSecondary,
    secondaryContainer = EmeraldSecondaryContainer,
    onSecondaryContainer = EmeraldOnSecondaryContainer,
    tertiary = EmeraldTertiary,
    onTertiary = EmeraldOnTertiary,
    tertiaryContainer = EmeraldTertiaryContainer,
    onTertiaryContainer = EmeraldOnTertiaryContainer,
    error = EmeraldError,
    onError = EmeraldOnError,
    errorContainer = EmeraldErrorContainer,
    onErrorContainer = EmeraldOnErrorContainer,
    background = EmeraldBackground,
    onBackground = EmeraldOnBackground,
    surface = EmeraldSurface,
    onSurface = EmeraldOnSurface,
)

private val EmeraldDarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = EmeraldOnPrimaryDark,
    primaryContainer = EmeraldPrimaryContainerDark,
    onPrimaryContainer = EmeraldOnPrimaryContainerDark,
    secondary = EmeraldSecondaryDark,
    onSecondary = EmeraldOnSecondaryDark,
    secondaryContainer = EmeraldSecondaryContainerDark,
    onSecondaryContainer = EmeraldOnSecondaryContainerDark,
    tertiary = EmeraldTertiaryDark,
    onTertiary = EmeraldOnTertiaryDark,
    tertiaryContainer = EmeraldTertiaryContainerDark,
    onTertiaryContainer = EmeraldOnTertiaryContainerDark,
    error = EmeraldError,
    background = Color(0xFF191C1A),
    surface = Color(0xFF191C1A),
    onBackground = Color(0xFFE1E3DF),
    onSurface = Color(0xFFE1E3DF),
)

@Composable
fun AppNameTheme(
    windowSizeClass: WindowSizeClass = rememberWindowSizeClass(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        @Suppress("NewApi")
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (darkTheme) {
        EmeraldDarkColorScheme
    } else {
        EmeraldLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Responsive Logic
    val orientation = if (windowSizeClass.width.size > windowSizeClass.height.size) {
        Orientation.Landscape
    } else {
        Orientation.Portrait
    }

    val sizeThatMatters = if (orientation == Orientation.Portrait) windowSizeClass.width else windowSizeClass.height

    val dimensions = when (sizeThatMatters) {
        is WindowSize.Small -> smallDimensions
        is WindowSize.Compact -> compactDimensions
        is WindowSize.Medium -> mediumDimensions
        else -> largeDimensions
    }

    ProvideAppUtils(dimensions = dimensions, orientation = orientation) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = myShapes,
            content = content
        )
    }
}
