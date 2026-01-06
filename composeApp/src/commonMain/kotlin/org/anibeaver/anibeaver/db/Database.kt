package org.anibeaver.anibeaver.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.db.daos.AnimeEntryDao
import org.anibeaver.anibeaver.db.daos.ReferenceDao
import org.anibeaver.anibeaver.db.daos.TagDao
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity
import org.anibeaver.anibeaver.db.entities.EntryTagEntity
import org.anibeaver.anibeaver.db.entities.ReferenceEntity
import org.anibeaver.anibeaver.db.entities.TagEntity


@Database(
    entities = [
        AnimeEntryEntity::class,
        TagEntity::class,
        EntryTagEntity::class,
        ReferenceEntity::class
    ],
    version = 6
)
@TypeConverters(DatabaseConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): AnimeEntryDao
    abstract fun tagDao(): TagDao
    abstract fun referenceDao(): ReferenceDao
}

class DatabaseConverters {
    @TypeConverter
    fun fromTagType(value: TagType?): String? = value?.name

    @TypeConverter
    fun toTagType(value: String?): TagType? = value?.let { TagType.valueOf(it) }

    @TypeConverter
    fun fromIntList(list: List<Int>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toIntList(csv: String?): List<Int> = csv
        ?.takeIf { it.isNotBlank() }
        ?.split(",")
        ?.mapNotNull { it.toIntOrNull() }
        ?: emptyList()
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}