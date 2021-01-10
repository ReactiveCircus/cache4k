package io.github.reactivecircus.cache4k.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusReference
import androidx.compose.ui.focus.focusReference
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.reactivecircus.cache4k.sample.shared.User
import io.github.reactivecircus.cache4k.sample.shared.userCache

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ScrollableColumn(modifier = Modifier.padding(16.dp)) {
                    SaveUser(onClickSave = { user -> userCache.put(user.id, user) })
                    Divider(modifier = Modifier.padding(vertical = 32.dp))
                    LoadUser(onClickLoad = { id -> userCache.get(id) })
                    Divider(modifier = Modifier.padding(vertical = 32.dp))
                    DeleteUser(onClickDelete = { id -> userCache.invalidate(id) })
                }
            }
        }
    }
}

@Composable
fun SaveUser(onClickSave: (User) -> Unit) {
    var user: User? by remember { mutableStateOf(null) }

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var shouldShowError by remember { mutableStateOf(false) }

    val focusReference = remember { FocusReference() }

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
            onImeActionPerformed = { _, _ -> focusReference.requestFocus() },
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.focusReference(focusReference),
            label = { Text("Name") },
            value = name,
            onValueChange = { name = it.also { buildUser() } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onImeActionPerformed = { _, controller ->
                user?.let { onClickSave(it) }
                shouldShowError = user == null
                controller?.hideSoftwareKeyboard()
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

@Composable
fun LoadUser(onClickLoad: (String) -> User?) {
    var user: User? by remember { mutableStateOf(null) }

    var initialLaunch by remember { mutableStateOf(true) }

    var id by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Load user", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text("Id") },
            value = id,
            onValueChange = { id = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onImeActionPerformed = { _, controller ->
                user = onClickLoad(id).also { initialLaunch = false }
                controller?.hideSoftwareKeyboard()
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

@Composable
fun DeleteUser(onClickDelete: (String) -> Unit) {
    var id by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Delete user", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            label = { Text("Id") },
            value = id,
            onValueChange = { id = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onImeActionPerformed = { _, controller ->
                onClickDelete(id)
                controller?.hideSoftwareKeyboard()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onClickDelete(id) },
            content = { Text("Delete") },
        )
    }
}
