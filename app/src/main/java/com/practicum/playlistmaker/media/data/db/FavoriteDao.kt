package com.practicum.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTrack(trackEntity: FavoriteEntity)

    @Delete
    suspend fun deleteFavoriteEntity(trackEntity: FavoriteEntity)

    @Query("SELECT * FROM favorite_table ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_table WHERE trackId = :trackId)")
    fun isInFavorites(trackId: Int): Flow<Boolean>

}
