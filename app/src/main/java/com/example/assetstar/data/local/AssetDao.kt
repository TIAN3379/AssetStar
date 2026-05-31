package com.example.assetstar.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY updatedAt DESC")
    fun observeAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :assetId")
    fun observeAssetById(assetId: Long): Flow<AssetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: AssetEntity): Long

    @Update
    suspend fun update(asset: AssetEntity)

    @Delete
    suspend fun delete(asset: AssetEntity)

    @Query("DELETE FROM assets")
    suspend fun clearAll()
}
