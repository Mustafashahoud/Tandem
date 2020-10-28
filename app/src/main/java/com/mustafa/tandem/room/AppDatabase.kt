package com.mustafa.tandem.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafa.tandem.model.Member

@Database(
    entities = [(Member::class)],
    version = 10, exportSchema = false
)
@TypeConverters(
    value = [(StringListConverter::class), (IntegerListConverter::class)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
}