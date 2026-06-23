package com.example.mobile_activity_02.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary    = Teal400,
    secondary  = Amber400,
    tertiary   = Green400,
    background = Navy900,
    surface    = Navy800,
    onPrimary  = Navy900,
    onSecondary = Navy900,
    onBackground = CardBg,
    onSurface  = CardBg
)

private val LightColorScheme = lightColorScheme(
    primary    = Teal500,
    secondary  = Amber500,
    tertiary   = Green500,
    background = Surface,
    surface    = CardBg,
    onPrimary  = CardBg,
    onSecondary = Navy900,
    onBackground = OnSurface,
    onSurface  = OnSurface
)

@Composable
fun Mobileactivity02Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,          // disabled so our brand palette is always used
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}