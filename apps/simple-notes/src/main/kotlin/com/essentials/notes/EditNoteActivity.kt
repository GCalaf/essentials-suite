package com.essentials.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Screen for creating or editing a single note.
 *
 * - If opened with a note ID extra, it loads that note for editing.
 * - If opened without extras, it creates a new note.
 * - The save button (FAB) saves and returns to the main screen.
 */
class EditNoteActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText

    private var existingNoteId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        repository = NoteRepository(this)

        // Find views
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        titleInput = findViewById(R.id.editTitle)
        contentInput = findViewById(R.id.editContent)
        val saveFab = findViewById<FloatingActionButton>(R.id.saveFab)

        // Set up toolbar with back button
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // If editing an existing note, load its data
        existingNoteId = intent.getLongExtra("NOTE_ID", -1L)
        if (existingNoteId != -1L) {
            val note = repository.loadAll().find { it.id == existingNoteId }
            if (note != null) {
                supportActionBar?.title = "Edit Note"
                titleInput.setText(note.title)
                contentInput.setText(note.content)
            }
        } else {
            supportActionBar?.title = "New Note"
        }

        // Save button
        saveFab.setOnClickListener { saveNote() }
    }

    private fun saveNote() {
        val title = titleInput.text.toString().trim()
        val content = contentInput.text.toString().trim()

        // Don't save completely empty notes
        if (title.isEmpty() && content.isEmpty()) {
            finish()
            return
        }

        val now = System.currentTimeMillis()
        val note = Note(
            id = if (existingNoteId != -1L) existingNoteId else now,
            title = title,
            content = content,
            createdAt = if (existingNoteId != -1L) {
                repository.loadAll().find { it.id == existingNoteId }?.createdAt ?: now
            } else now,
            modifiedAt = now
        )

        repository.save(note)
        setResult(Activity.RESULT_OK, Intent().putExtra("NOTE_ID", note.id))
        finish()
    }
}
