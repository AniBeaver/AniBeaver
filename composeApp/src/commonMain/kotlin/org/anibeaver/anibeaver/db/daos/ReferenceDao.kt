package org.anibeaver.anibeaver.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.anibeaver.anibeaver.db.entities.ReferenceEntity

@Dao
interface ReferenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(references: List<ReferenceEntity>)

    @Query("SELECT * FROM ReferenceEntity WHERE entryId = :entryId ORDER BY orderIndex ASC")
    suspend fun getByEntryId(entryId: Int): List<ReferenceEntity>

    @Query("DELETE FROM ReferenceEntity WHERE entryId = :entryId")
    suspend fun deleteByEntryId(entryId: Int)
}
