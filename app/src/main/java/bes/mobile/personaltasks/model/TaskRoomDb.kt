package bes.mobile.personaltasks.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class TaskRoomDb : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}