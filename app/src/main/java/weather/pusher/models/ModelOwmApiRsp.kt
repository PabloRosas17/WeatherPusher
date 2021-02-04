package weather.pusher.models

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class OwmRsp(
    @SerializedName("cod")
    val cod: String = "",
    @SerializedName("message")
    val message: Int = 0,
    @SerializedName("cnt")
    val cnt: Int = 0,
    @SerializedName("list")
    val list: JsonArray,
    @SerializedName("city")
    val city: JsonObject
)

data class OwmRspWeather(
    val dt: Int = 0
    , val main: JsonObject
    , val weather: JsonArray
    , val clouds: JsonObject
    , val wind: JsonObject
    , val sys: JsonObject
    , val dt_txt: String = ""
)

data class OwmWeatherMain(
    val temp: Double = 0.00
    , val feels_like: Double = 0.00
    , val temp_min: Double = 0.00
    , val temp_max: Double = 0.00
    , val pressure: Double = 0.00
    , val sea_level: Double = 0.00
    , val grnd_level: Double = 0.00
    , val humidity: Double = 0.00
    , val temp_kf: Double = 0.00
)

data class WeatherData(
    val client_date: String = ""
    , val client_time: String = ""
    , val client_min: Double = 0.00
    , val client_max: Double = 0.00
)