package com.practicum.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewTrack(trackEntity: FavoriteEntity)

    @Delete
    fun deleteFavoriteEntity(trackEntity: FavoriteEntity)

    @Query("SELECT * FROM favorite_table")
    fun getFavorites(): List<FavoriteEntity>

    @Query("SELECT trackId FROM favorite_table")
    fun getFavoriteTrackIds(): List<Long>
}
