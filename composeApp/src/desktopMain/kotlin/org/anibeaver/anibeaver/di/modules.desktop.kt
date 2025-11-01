package org.anibeaver.anibeaver.di

import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.DesktopApiAuthorizationHandler
import org.anibeaver.anibeaver.api.ApiAuthorizationHandler
import org.anibeaver.anibeaver.api.tokenStore
import org.anibeaver.anibeaver.api.TokenStore
import org.anibeaver.anibeaver.db.AppDatabase
import org.anibeaver.anibeaver.db.getDatabaseBuilder

import androidx.room.RoomDatabase

import org.koin.dsl.module

actual val platformModule = module{
    single<ApiAuthorizationHandler>{DesktopApiAuthorizationHandler()}
    single{ApiHandler(get())}
    single<TokenStore>{tokenStore("org.anibeaver.anibeaver", "anilist", platformContext = null)}
    single<RoomDatabase.Builder<AppDatabase>>{getDatabaseBuilder()}
}