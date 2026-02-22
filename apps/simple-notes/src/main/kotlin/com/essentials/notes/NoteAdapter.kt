package com.essentials.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RecyclerView adapter for displaying notes in a scrollable list.
 *
 * Uses ListAdapter with DiffUtil for efficient updates —
 * only the changed items are re-rendered, not the whole list.
 *
 * @param onNoteClick Called when the user taps a note (to edit it).
 * @param onNoteLongClick Called when the user long-presses a note (to delete it).
 */
class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.noteTitle)
        private val previewView: TextView = itemView.findViewById(R.id.notePreview)
        private val dateView: TextView = itemView.findViewById(R.id.noteDate)

        fun bind(note: Note) {
            titleView.text = note.title.ifEmpty { "Untitled" }
            previewView.text = note.content.take(100)
            dateView.text = formatDate(note.modifiedAt)

            itemView.setOnClickListener { onNoteClick(note) }
            itemView.setOnLongClickListener {
                onNoteLongClick(note)
                true
            }
        }

        private fun formatDate(millis: Long): String {
            val formatter = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())
            return formatter.format(Date(millis))
        }
    }

    private class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem == newItem
    }
}
