package com.kwabenaberko.currencyconverter.android.converter.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.converter.model.ConversionMode
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme
import com.kwabenaberko.currencyconverter.android.theme.RedColorScheme
import com.kwabenaberko.currencyconverter.android.theme.WhiteColorScheme

@Composable
internal fun ConversionDirection(
    mode: ConversionMode,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.background(WhiteColorScheme.primary, CircleShape)
            .placeholder(
                visible = false,
                color = Color(0xFF979797),
                shape = CircleShape,
                highlight = PlaceholderHighlight.fade()
            )
            .border(BorderStroke(6.dp, RedColorScheme.primary), CircleShape)
            .padding(20.dp)
    ) {
        Icon(
            painter = when (mode) {
                ConversionMode.FIRST_MONEY_TO_SECOND_MONEY -> {
                    painterResource(R.drawable.ic_long_arrow_down)
                }
                ConversionMode.SECOND_MONEY_TO_FIRST_MONEY -> {
                    painterResource(R.drawable.ic_long_arrow_up)
                }
            },
            contentDescription = null,
            tint = RedColorScheme.primary
        )
    }
}

@Preview
@Composable
private fun ConversionDirectionPreview(){
    CurrencyConverterTheme {
        ConversionDirection(ConversionMode.FIRST_MONEY_TO_SECOND_MONEY)
    }
}