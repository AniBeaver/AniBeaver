package org.anibeaver.anibeaver.db.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.db.entities.EntryTagEntity
import org.anibeaver.anibeaver.db.entities.TagEntity
import org.anibeaver.anibeaver.db.relations.AnimeEntryWithTags

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTag(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTags(tags: List<TagEntity>): List<Long>

    @Transaction
    @Query("SELECT * FROM TagEntity")
    fun getAllTagsFlow(): Flow<List<TagEntity>>

    @Query("SELECT * FROM TagEntity")
    suspend fun getAllTags(): List<TagEntity>

    @Query("SELECT * FROM TagEntity WHERE id = :id")
    suspend fun getTagById(id: Int): TagEntity?

    @Query("SELECT * FROM TagEntity WHERE name = :name AND type = :type")
    suspend fun getTagByNameAndType(name: String, type: TagType): TagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntryTags(links: List<EntryTagEntity>)

    @Query("DELETE FROM EntryTagEntity WHERE entryId = :entryId")
    suspend fun clearEntryTags(entryId: Int)

    @Transaction
    @Query("SELECT * FROM AnimeEntryEntity")
    fun getEntriesWithTagsFlow(): Flow<List<AnimeEntryWithTags>>

    @Transaction
    @Query("SELECT * FROM AnimeEntryEntity")
    suspend fun getEntriesWithTags(): List<AnimeEntryWithTags>

    @Transaction
    @Query("SELECT * FROM AnimeEntryEntity WHERE id = :entryId")
    suspend fun getEntryWithTags(entryId: Int): AnimeEntryWithTags?

    @Delete
    suspend fun deleteTag(tag: TagEntity)
}

