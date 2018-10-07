package amarenkov.movieraid.room

import amarenkov.movieraid.utils.getFromKoin
import amarenkov.movieraid.models.CastMember
import amarenkov.movieraid.models.CrewMember
import amarenkov.movieraid.models.Genre
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromListOfStringsToString(value: List<String>): String {
        return getFromKoin<Gson>().toJson(value)
    }

    @TypeConverter
    fun fromStringToListOfStrings(value: String): List<String> {
        return getFromKoin<Gson>().fromJson<List<String>>(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromListOfIntsToString(value: List<Int>): String {
        return getFromKoin<Gson>().toJson(value)
    }

    @TypeConverter
    fun fromStringToListOfInts(value: String): List<Int> {
        return getFromKoin<Gson>().fromJson<List<Int>>(value, object : TypeToken<List<Int>>() {}.type)
    }

    @TypeConverter
    fun fromListOfCastMembersToString(value: List<CastMember>): String {
        return getFromKoin<Gson>().toJson(value)
    }

    @TypeConverter
    fun fromStringToListOfCastMembers(value: String): List<CastMember> {
        return getFromKoin<Gson>().fromJson<List<CastMember>>(value, object : TypeToken<List<CastMember>>() {}.type)
    }

    @TypeConverter
    fun fromListOfCrewMembersToString(value: List<CrewMember>): String {
        return getFromKoin<Gson>().toJson(value)
    }

    @TypeConverter
    fun fromStringToListOfCrewMembers(value: String): List<CrewMember> {
        return getFromKoin<Gson>().fromJson<List<CrewMember>>(value, object : TypeToken<List<CrewMember>>() {}.type)
    }

    @TypeConverter
    fun fromListOfGenresToString(value: List<Genre>): String {
        return getFromKoin<Gson>().toJson(value)
    }

    @TypeConverter
    fun fromStringToListOfGenres(value: String): List<Genre> {
        return getFromKoin<Gson>().fromJson<List<Genre>>(value, object : TypeToken<List<Genre>>() {}.type)
    }
}