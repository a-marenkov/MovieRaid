package amarenkov.movieraid.base

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
abstract class BaseEntity(@PrimaryKey(autoGenerate = true) var roomId: Long = -1L)