package amarenkov.movieraid.room.repos

import amarenkov.movieraid.room.db.AppDatabase
import amarenkov.movieraid.room.models.Movie
import amarenkov.movieraid.room.models.MovieDetailed
import androidx.room.*

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun save(movie: MovieDetailed) : Long

    @Query("SELECT * FROM ${AppDatabase.MOVIES_TABLE} WHERE id = :id")
    fun get(id: Long) : MovieDetailed?

    @Update
    fun update(movie: MovieDetailed)

    @Query("SELECT id, title, rating, date, poster FROM ${AppDatabase.MOVIES_TABLE} WHERE isFavorite = 1")
    fun getFavoriteMovies(): List<Movie>

    @Query("UPDATE ${AppDatabase.MOVIES_TABLE} SET isFavorite = 0 WHERE id = :id")
    fun removeFromFavorites(id: Long)

    @Query("UPDATE ${AppDatabase.MOVIES_TABLE} SET isFavorite = 1 WHERE id = :id")
    fun setAsFavorite(id: Long)


    @Query("DELETE FROM ${AppDatabase.MOVIES_TABLE} WHERE isFavorite = 0 AND timestamp < :pivot")
    fun clearCache(pivot: Long)

    @Query("SELECT id FROM ${AppDatabase.MOVIES_TABLE}")
    fun getAll() : List<Long>
}