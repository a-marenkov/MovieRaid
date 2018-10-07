package amarenkov.movieraid.models

import amarenkov.movieraid.R
import amarenkov.movieraid.base.BaseEntity
import amarenkov.movieraid.room.AppDatabase
import amarenkov.movieraid.utils.convertToMoneyString
import amarenkov.movieraid.utils.getPluralString
import amarenkov.movieraid.utils.now
import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.google.gson.annotations.SerializedName

class Movie(var id: Long = -1,
            var title: String = "",
            @Ignore var overview: String = "",
            @SerializedName("vote_average") var rating: Float = -1f,
            @SerializedName("release_date") var date: String = "",
            @Ignore @SerializedName("genre_ids") var genreIds: List<Int> = listOf(),
            @SerializedName("poster_path") var poster: String = "") {

    val titleAndYear: String
        get() {
            val year = if (date.length > 4) " (${date.substring(0, 4)})" else ""
            return "$title$year"
        }
}

@Entity(tableName = AppDatabase.MOVIES_TABLE, indices = [Index("id")])
data class MovieDetailed(val id: Long,
                         val title: String,
                         val overview: String,
                         @SerializedName("release_date") val date: String,
                         @SerializedName("original_title") val titleOriginal: String,
                         @SerializedName("original_language") val language: String,
                         val genres: List<Genre>,
                         @SerializedName("poster_path") val poster: String,
                         @SerializedName("backdrop_path") val backdrop: String = "",
                         @SerializedName("vote_average") val rating: Float,
                         @SerializedName("vote_count") val voteCount: Int,
                         @SerializedName("adult") val isAdult: Boolean,
                         val popularity: Float,
                         val budget: Int,
                         val homepage: String,
                         val revenue: Int,
                         val runtime: Int,
                         val tagline: String,
                         @Embedded val credits: Credits,
                         var isFavorite: Boolean = false,
                         var isWatchLater: Boolean = false,
                         var timestamp: Long = now) : BaseEntity() {

    fun getRating(resources: Resources) = resources.getString(R.string.rating_and_votes, rating, voteCount,
            getPluralString(resources, voteCount, R.string.vote0, R.string.vote1, R.string.vote2))

    fun getDuration(resources: Resources) =
            if (runtime == 0) resources.getString(R.string.movie_duration_no_data)
            else resources.getString(R.string.movie_duration, runtime, getPluralString(
                    resources, runtime, R.string.minute0, R.string.minute1, R.string.minute2))

    fun getGenres(resources: Resources): String {
        genres.map { it.name }.toString().let { it.substring(1, it.length - 1) }.let {
            return if (it.isBlank() || it == "null") resources.getString(R.string.movie_genres_no_data)
            else resources.getString(R.string.movie_genres, it)
        }
    }

    fun getBudget(resources: Resources) = if (budget == 0) resources.getString(R.string.movie_budget_no_data)
    else resources.getString(R.string.movie_budget, budget.convertToMoneyString())

    fun getCast(resources: Resources): String {
        credits.cast.map { it -> it.name + "(${it.character})" }.toString()
                .let { it.substring(1, it.length - 1).replace("()", "") }
                .let {
                    return if (it.isBlank()) resources.getString(R.string.cast_no_data)
                    else resources.getString(R.string.cast, it)
                }
    }

    fun getCrew(resources: Resources): String {
        credits.crew.map { it -> it.name + "(${it.job})" }.toString()
                .let { it.substring(1, it.length - 1).replace("()", "") }
                .let {
                    return if (it.isBlank()) resources.getString(R.string.crew_no_data)
                    else resources.getString(R.string.crew, it)
                }
    }
}

data class Credits(val cast: List<CastMember>,
                   val crew: List<CrewMember>)

data class CastMember(val name: String,
                      val character: String)

data class CrewMember(val name: String,
                      val job: String)

class MovieSearchResponse(val page: Int,
                          @SerializedName("total_pages") val pagesCount: Int,
                          val results: List<Movie> = listOf())