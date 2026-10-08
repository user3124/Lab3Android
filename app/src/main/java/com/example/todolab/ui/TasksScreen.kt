package com.example.todolab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolab.data.Task
import com.example.todolab.ui.theme.CardWhite
import com.example.todolab.ui.theme.CoralButton
import com.example.todolab.ui.theme.SkyButton
import com.example.todolab.ui.theme.TealPrimary
import com.example.todolab.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(viewModel: TasksViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val lastSync by viewModel.lastSync.collectAsStateWithLifecycle()
    var newTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Мои задачи",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatLastSync(lastSync),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(Modifier.height(16.dp))

        // Поле ввода + кнопка добавления
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Новая задача", style = MaterialTheme.typography.bodyMedium) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    cursorColor = TealPrimary,
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite
                )
            )
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = {
                    viewModel.addTask(newTitle)
                    newTitle = ""
                },
                modifier = Modifier.height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = CoralButton)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Кнопка ручной синхронизации
        Button(
            onClick = { viewModel.syncNow() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = SkyButton)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Синхронизировать", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Text(
                text = "Задач пока нет. Добавь первую или нажми «Синхронизировать».",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.toggle(task) },
                        onDelete = { viewModel.delete(task) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
            )
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.completed) TextMuted else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = CoralButton
                )
            }
        }
    }
}

private fun formatLastSync(time: Long): String {
    if (time == 0L) return "Синхронизации ещё не было"
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return "Последняя синхронизация: ${formatter.format(Date(time))}"
}