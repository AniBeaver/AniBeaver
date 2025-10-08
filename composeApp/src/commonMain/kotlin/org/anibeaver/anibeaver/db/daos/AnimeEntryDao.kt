package org.anibeaver.anibeaver.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity

@Dao
interface AnimeEntryDao {
    @Insert
    suspend fun insert(item: AnimeEntryEntity)

    @Query("SELECT count(*) FROM AnimeEntryEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM AnimeEntryEntity")
    fun getAllAsFlow(): Flow<List<AnimeEntryEntity>>
}