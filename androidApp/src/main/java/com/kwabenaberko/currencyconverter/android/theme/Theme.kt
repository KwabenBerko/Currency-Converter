package com.kwabenaberko.currencyconverter.android.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val RedColorScheme = lightColorScheme(
    background = red_primary,
    primary = red_primary,
    onPrimary = red_onPrimary,
    secondary = red_secondary,
    surface = red_primary,
    outlineVariant = red_secondary
)

val WhiteColorScheme = lightColorScheme(
    background = white_primary,
    primary = white_primary,
    onPrimary = white_onPrimary,
    secondary = white_secondary,
    surface = white_primary,
    outlineVariant = white_secondary,
)

@Composable
fun CurrencyConverterTheme(
    useRedTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useRedTheme) {
            RedColorScheme
        } else {
            WhiteColorScheme
        },
        typography = Typography,
        content = content
    )
}
