package amarenkov.movieraid.room.models

import amarenkov.movieraid.base.BaseEntity
import amarenkov.movieraid.room.db.AppDatabase
import androidx.room.Entity

@Entity(tableName = AppDatabase.GENRES_TABLE)
class Genre(val id: Int, val name: String) : BaseEntity()

class GenresResponse(val genres: List<Genre>)