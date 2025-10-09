package org.anibeaver.anibeaver.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.anibeaver.anibeaver.db.entities.EntryStudioEntity

@Dao
interface EntryStudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<EntryStudioEntity>)

    @Query("SELECT studioId FROM EntryStudioEntity WHERE entryId = :entryId")
    suspend fun getStudioIdsByEntryId(entryId: Int): List<Int>

    @Query("DELETE FROM EntryStudioEntity WHERE entryId = :entryId")
    suspend fun deleteByEntryId(entryId: Int)
}
