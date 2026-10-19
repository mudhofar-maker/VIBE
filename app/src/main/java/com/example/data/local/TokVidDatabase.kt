package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VideoInteractionEntity::class,
        UserCommentEntity::class,
        FollowedCreatorEntity::class,
        CreatedVideoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TokVidDatabase : RoomDatabase() {
    abstract fun tokVidDao(): TokVidDao

    companion object {
        @Volatile
        private var INSTANCE: TokVidDatabase? = null

        fun getDatabase(context: Context): TokVidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TokVidDatabase::class.java,
                    "tokvid_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
