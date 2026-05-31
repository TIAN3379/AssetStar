package com.example.assetstar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AssetEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AssetDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao

    companion object {
        fun create(context: Context): AssetDatabase {
            return Room.databaseBuilder(
                context = context.applicationContext,
                klass = AssetDatabase::class.java,
                name = "asset_star.db",
            ).build()
        }
    }
}
