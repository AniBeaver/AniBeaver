package org.anibeaver.anibeaver.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity

@Dao
interface AnimeEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: AnimeEntryEntity): Long

    @Query("SELECT count(*) FROM AnimeEntryEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM AnimeEntryEntity")
    fun getAllAsFlow(): Flow<List<AnimeEntryEntity>>

    @Query("SELECT * FROM AnimeEntryEntity")
    suspend fun getAll(): List<AnimeEntryEntity>

    @Query("SELECT * FROM AnimeEntryEntity WHERE id = :id")
    suspend fun getById(id: Long): AnimeEntryEntity?

    @Query("DELETE FROM AnimeEntryEntity WHERE id = :id")
    suspend fun deleteById(id: Long)
}