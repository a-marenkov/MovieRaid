package amarenkov.movieraid.room.db

import amarenkov.movieraid.room.models.Genre
import amarenkov.movieraid.room.models.MovieDetailed
import amarenkov.movieraid.room.repos.GenresDao
import amarenkov.movieraid.room.repos.MoviesDao
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