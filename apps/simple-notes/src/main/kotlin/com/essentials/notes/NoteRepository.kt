package com.essentials.notes

import android.content.Context
import org.json.JSONArray
import java.io.File

/**
 * Handles persistence of notes using plain JSON files stored
 * in the app's private internal storage.
 *
 * Why JSON files instead of a database?
 * - Zero dependencies (no Room, no SQLite boilerplate)
 * - Human-readable data (easy to debug)
 * - Perfectly suitable for hundreds of notes
 * - Aligns with the minimalist philosophy
 *
 * Notes are stored in a single file: notes.json
 */
class NoteRepository(context: Context) {

    private val file = File(context.filesDir, "notes.json")

    /** Load all notes from disk, sorted newest-first. */
    fun loadAll(): List<Note> {
        if (!file.exists()) return emptyList()

        return try {
            val json = JSONArray(file.readText())
            (0 until json.length())
                .map { Note.fromJson(json.getJSONObject(it)) }
                .sortedByDescending { it.modifiedAt }
        } catch (e: Exception) {
            // If the file is corrupted, return empty rather than crash
            emptyList()
        }
    }

    /** Save the full list of notes to disk (replaces the file). */
    private fun saveAll(notes: List<Note>) {
        val json = JSONArray()
        notes.forEach { json.put(it.toJson()) }
        file.writeText(json.toString(2)) // Pretty-print with 2-space indent
    }

    /** Add or update a note. If a note with the same ID exists, it's replaced. */
    fun save(note: Note) {
        val notes = loadAll().toMutableList()
        val index = notes.indexOfFirst { it.id == note.id }
        if (index >= 0) {
            notes[index] = note
        } else {
            notes.add(note)
        }
        saveAll(notes)
    }

    /** Delete a note by its ID. */
    fun delete(noteId: Long) {
        val notes = loadAll().filter { it.id != noteId }
        saveAll(notes)
    }
}
