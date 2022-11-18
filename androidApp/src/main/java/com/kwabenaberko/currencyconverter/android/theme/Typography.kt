package com.kwabenaberko.currencyconverter.android.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.kwabenaberko.currencyconverter.android.R

val SanFrancisco = FontFamily(Font(R.font.sf_regular))

val Typography = Typography(
    labelLarge = TextStyle(
        fontFamily = SanFrancisco,
        fontSize = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SanFrancisco,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SanFrancisco,
        fontSize = 14.sp
    ),
)
