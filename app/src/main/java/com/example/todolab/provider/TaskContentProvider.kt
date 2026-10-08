package com.example.todolab.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.todolab.data.AppDatabase
import com.example.todolab.data.Task
import com.example.todolab.notifications.Notifier

class TaskContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.todolab.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/tasks")

        private const val CODE_TASKS = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "tasks", CODE_TASKS)
        }
    }

    private val dao
        get() = AppDatabase.getInstance(context!!).taskDao()

    override fun onCreate(): Boolean = true

    // Чтение списка задач. Колонки: id, title, completed (0/1)
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        if (uriMatcher.match(uri) != CODE_TASKS) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
        val cursor = dao.getAllCursor()
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    // Добавление задачи. Ожидаемые значения: title (обязательно), completed (необязательно)
    override fun insert(uri: Uri, values: ContentValues?): Uri {
        if (uriMatcher.match(uri) != CODE_TASKS) {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (values == null) {
            throw IllegalArgumentException("ContentValues must not be null")
        }
        val title = values.getAsString("title")?.trim()
        if (title.isNullOrEmpty()) {
            throw IllegalArgumentException("Field 'title' is required")
        }
        val completed = values.getAsBoolean("completed") ?: false

        val id = dao.insertBlocking(Task(title = title, completed = completed))

        val ctx = context!!
        ctx.contentResolver.notifyChange(CONTENT_URI, null)

        // Уведомляем, только если вставка пришла из ДРУГОГО приложения
        if (callingPackage != ctx.packageName) {
            Notifier.notifyTaskAdded(ctx, title)
        }

        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    // Изменение и удаление извне запрещены: безопасный доступ только на чтение и добавление
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String =
        "vnd.android.cursor.dir/vnd.$AUTHORITY.tasks"
}