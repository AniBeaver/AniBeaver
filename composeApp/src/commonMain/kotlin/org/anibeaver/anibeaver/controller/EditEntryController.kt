package org.anibeaver.anibeaver.controller

import org.anibeaver.anibeaver.model.Entry

object EditEntryController {
    fun handleEditEntry(entry: Entry) {
        println("[Controller] Entry: $entry")
        //TODO: Update the correct entry here; Potentially even move this to entries controller
    }
}
