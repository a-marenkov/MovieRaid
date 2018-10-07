package amarenkov.movieraid.room.dao

import amarenkov.movieraid.models.Genre
import amarenkov.movieraid.room.AppDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GenresDao {
    @Insert
    fun saveAll(genres: List<Genre>)

    @Query("SELECT * FROM ${AppDatabase.GENRES_TABLE}")
    fun getAll() : List<Genre>
}