package org.anibeaver.anibeaver

import androidx.room.RoomDatabase
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.TokenStore
import org.anibeaver.anibeaver.db.AppDatabase

class DataWrapper(
    val activityKiller: () -> Unit = {}
)