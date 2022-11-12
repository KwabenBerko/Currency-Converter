package com.kwabenaberko.currencyconverter.android.keypad

import androidx.compose.ui.text.input.EditCommand
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.PlatformTextInputService
import androidx.compose.ui.text.input.TextFieldValue

class KeyPadInputService : PlatformTextInputService {
    override fun hideSoftwareKeyboard() {
        TODO("Not yet implemented")
    }

    override fun showSoftwareKeyboard() {
        TODO("Not yet implemented")
    }

    override fun startInput(
        value: TextFieldValue,
        imeOptions: ImeOptions,
        onEditCommand: (List<EditCommand>) -> Unit,
        onImeActionPerformed: (ImeAction) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun stopInput() {
        TODO("Not yet implemented")
    }

    override fun updateState(oldValue: TextFieldValue?, newValue: TextFieldValue) {
        TODO("Not yet implemented")
    }
}
