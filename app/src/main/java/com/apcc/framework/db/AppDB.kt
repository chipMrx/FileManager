package com.apcc.framework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.apcc.data.*

@Database(
    entities = [
        ErrorTracking::class,
        FileApp::class,
        Option::class,
        DelaySyncData::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {

    abstract fun emmaDao(): AppDao

}