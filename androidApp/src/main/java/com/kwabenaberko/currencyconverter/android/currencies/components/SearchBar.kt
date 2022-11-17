package com.kwabenaberko.currencyconverter.android.currencies.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kwabenaberko.currencyconverter.android.R
import com.kwabenaberko.currencyconverter.android.theme.CurrencyConverterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchBar(
    initialValue: String,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onClose: () -> Unit = {}
) {

    val (query, setQuery) = rememberSaveable { mutableStateOf(initialValue) }
    val focusRequester = remember { FocusRequester() }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose {
            focusRequester.freeFocus()
        }
    }

    TextField(
        value = query,
        onValueChange = { value ->
            setQuery(value)
            onQueryChange(value)
        },
        textStyle = MaterialTheme.typography.labelMedium,
        modifier = Modifier.focusRequester(focusRequester).then(modifier),
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        trailingIcon = {
            IconButton(
                onClick = {
                    if (query.isNotEmpty()) {
                        onQueryChange("")
                        setQuery("")
                    } else {
                        onClose()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close Search Bar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        shape = RoundedCornerShape(8.dp)
    )
}

@Preview
@Composable
private fun SearchBarPreview() {
    CurrencyConverterTheme(useRedTheme = true) {
        SearchBar(initialValue = "Ghanaian Cedi", modifier = Modifier.fillMaxWidth())
    }
}
