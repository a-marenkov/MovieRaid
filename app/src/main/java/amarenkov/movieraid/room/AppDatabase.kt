package amarenkov.movieraid.room

import amarenkov.movieraid.models.Genre
import amarenkov.movieraid.models.MovieDetailed
import amarenkov.movieraid.repo.MoviesDao
import amarenkov.movieraid.room.dao.GenresDao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MovieDetailed::class, Genre::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val NAME = "db_movie_raid"
        const val MOVIES_TABLE = "movies"
        const val GENRES_TABLE = "genres"
    }

    abstract fun genresDao(): GenresDao
    abstract fun moviesDao(): MoviesDao
}