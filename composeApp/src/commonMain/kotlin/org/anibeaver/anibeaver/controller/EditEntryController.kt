package org.anibeaver.anibeaver.controller

import org.anibeaver.anibeaver.model.Entry

object EditEntryController {
    fun handleEditEntry(entry: Entry) {
        println("[Controller] Entry: $entry")
        // Add backend logic here (e.g., save, send to server, etc.)
    }
}
