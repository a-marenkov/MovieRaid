package amarenkov.movieraid.models

import amarenkov.movieraid.base.BaseEntity
import amarenkov.movieraid.room.AppDatabase
import androidx.room.Entity

@Entity(tableName = AppDatabase.GENRES_TABLE)
class Genre(val id: Int, val name: String) : BaseEntity()

class GenresResponse(val genres: List<Genre>)