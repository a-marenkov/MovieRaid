package amarenkov.movieraid.models

import com.google.gson.annotations.SerializedName

class Configuration(val images: ImageConfiguration)

class ImageConfiguration(@SerializedName("secure_base_url") val baseUrl: String)
