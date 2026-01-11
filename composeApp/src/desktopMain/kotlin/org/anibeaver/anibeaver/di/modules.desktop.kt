package org.anibeaver.anibeaver.di

import androidx.room.RoomDatabase
import org.anibeaver.anibeaver.api.*
import org.anibeaver.anibeaver.db.AppDatabase
import org.anibeaver.anibeaver.db.getDatabaseBuilder
import org.koin.dsl.module

actual val platformModule = module {
    single<ApiAuthorizationHandler> { DesktopApiAuthorizationHandler() }
    single { ApiHandler(get()) }
    single<TokenStore> { tokenStore("org.anibeaver.anibeaver", "anilist", platformContext = null) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}