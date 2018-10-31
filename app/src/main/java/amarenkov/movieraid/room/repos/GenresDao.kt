package amarenkov.movieraid.room.repos

import amarenkov.movieraid.room.db.AppDatabase
import amarenkov.movieraid.room.models.Genre
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