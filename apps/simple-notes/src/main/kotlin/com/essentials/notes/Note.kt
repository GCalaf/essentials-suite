package com.essentials.notes

import org.json.JSONObject

/**
 * Represents a single note.
 *
 * @property id Unique identifier (timestamp-based).
 * @property title Short title for the note.
 * @property content The full note body text.
 * @property createdAt Epoch millis when the note was created.
 * @property modifiedAt Epoch millis when the note was last modified.
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val modifiedAt: Long
) {
    /** Serialize this note to a JSON object. */
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("content", content)
        put("createdAt", createdAt)
        put("modifiedAt", modifiedAt)
    }

    companion object {
        /** Deserialize a note from a JSON object. */
        fun fromJson(json: JSONObject): Note = Note(
            id = json.getLong("id"),
            title = json.getString("title"),
            content = json.getString("content"),
            createdAt = json.getLong("createdAt"),
            modifiedAt = json.getLong("modifiedAt")
        )
    }
}
