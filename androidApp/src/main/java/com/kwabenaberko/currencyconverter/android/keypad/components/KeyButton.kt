package com.kwabenaberko.currencyconverter.android.keypad.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@Composable
internal fun KeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .size(68.dp)
            .clickable(onClick = onClick)
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
internal fun DoneKeyButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    KeyButton(
        onClick = onClick,
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Done",
            modifier = Modifier.size(36.dp)
                .align(Alignment.CenterHorizontally),
            tint = if (isEnabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            }
        )
    }
}

@Composable
internal fun TextKeyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    KeyButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 26.sp),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun DoneKeyButtonPreview() {
    CurrencyConverterTheme {
        DoneKeyButton()
    }
}

@Preview
@Composable
private fun TextKeyButtonPreview() {
    CurrencyConverterTheme {
        TextKeyButton(text = "9")
    }
}
