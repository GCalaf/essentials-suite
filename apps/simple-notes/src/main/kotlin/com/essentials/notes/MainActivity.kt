package com.essentials.notes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Main screen of Simple Notes.
 *
 * Displays a list of all saved notes. The user can:
 * - Tap the "+" button to create a new note
 * - Tap a note to edit it
 * - Long-press a note to delete it (with confirmation)
 *
 * Notes are loaded from and saved to local JSON files —
 * no internet, no database, no permissions required.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var repository: NoteRepository
    private lateinit var adapter: NoteAdapter
    private lateinit var emptyView: TextView

    // Modern replacement for startActivityForResult
    private val editNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ -> refreshNotes() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = NoteRepository(this)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the note list
        val recyclerView = findViewById<RecyclerView>(R.id.notesList)
        emptyView = findViewById(R.id.emptyView)

        adapter = NoteAdapter(
            onNoteClick = { note -> openEditor(note.id) },
            onNoteLongClick = { note -> confirmDelete(note) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Floating "+" button to create a new note
        val fab = findViewById<FloatingActionButton>(R.id.addNoteFab)
        fab.setOnClickListener { openEditor() }
    }

    override fun onResume() {
        super.onResume()
        refreshNotes()
    }

    /** Reload notes from storage and update the list. */
    private fun refreshNotes() {
        val notes = repository.loadAll()
        adapter.submitList(notes)

        // Show "no notes yet" message when the list is empty
        emptyView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }

    /** Open the editor screen, optionally with an existing note ID. */
    private fun openEditor(noteId: Long = -1L) {
        val intent = Intent(this, EditNoteActivity::class.java)
        if (noteId != -1L) {
            intent.putExtra("NOTE_ID", noteId)
        }
        editNoteLauncher.launch(intent)
    }

    /** Show a confirmation dialog before deleting a note. */
    private fun confirmDelete(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete note?")
            .setMessage("\"${note.title.ifEmpty { "Untitled" }}\" will be permanently deleted.")
            .setPositiveButton("Delete") { _, _ ->
                repository.delete(note.id)
                refreshNotes()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
