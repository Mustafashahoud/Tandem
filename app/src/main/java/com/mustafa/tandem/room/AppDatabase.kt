package com.mustafa.tandem.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.RemoteKeys

@Database(
    entities = [Member::class, RemoteKeys::class],
    version = 16, exportSchema = false
)
@TypeConverters(value = [(StringListConverter::class)])
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}