package io.github.reactivecircus.cache4k.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.reactivecircus.cache4k.sample.shared.User
import io.github.reactivecircus.cache4k.sample.shared.userCache

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        SaveUser(onClickSave = { user -> userCache.put(user.id, user) })
                    }
                    item {
                        Divider(modifier = Modifier.padding(vertical = 32.dp))
                    }
                    item {
                        LoadUser(onClickLoad = { id -> userCache.get(id) })
                    }
                    item {
                        Divider(modifier = Modifier.padding(vertical = 32.dp))
                    }
                    item {
                        DeleteUser(onClickDelete = { id -> userCache.invalidate(id) })
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun SaveUser(onClickSave: (User) -> Unit) {
    var user: User? by remember { mutableStateOf(null) }

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var shouldShowError by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current

    fun buildUser() {
        if (listOf(id, name).all(String::isNotBlank)) user = User(id, name)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Save user", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = id,
            label = { Text("Id") },
            onValueChange = { id = it.also { buildUser() } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions {
                focusRequester.requestFocus()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            label = { Text("Name") },
            value = name,
            onValueChange = { name = it.also { buildUser() } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions {
                user?.let { onClickSave(it) }
                shouldShowError = user == null
                keyboardController?.hide()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                user?.let { onClickSave(it) }
                shouldShowError = user == null
            },
            content = { Text("Save") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (shouldShowError) Text("This user can't be saved")
    }
}

@ExperimentalComposeUiApi
@Composable
fun LoadUser(onClickLoad: (String) -> User?) {
    var user: User? by remember { mutableStateOf(null) }

    var initialLaunch by remember { mutableStateOf(true) }

    val keyboardController = LocalSoftwareKeyboardController.current

    var id by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Load user", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text("Id") },
            value = id,
            onValueChange = { id = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions {
                user = onClickLoad(id).also { initialLaunch = false }
                keyboardController?.hide()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { user = onClickLoad(id).also { initialLaunch = false } },
            content = { Text("Load") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!initialLaunch) Text(user?.name ?: "Not found", style = MaterialTheme.typography.h5)
    }
}

@ExperimentalComposeUiApi
@Composable
fun DeleteUser(onClickDelete: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var id by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Delete user", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text("Id") },
            value = id,
            onValueChange = { id = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions {
                onClickDelete(id)
                keyboardController?.hide()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onClickDelete(id) },
            content = { Text("Delete") },
        )
    }
}
