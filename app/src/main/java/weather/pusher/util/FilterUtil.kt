package weather.pusher.util

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import com.google.gson.Gson
import com.google.gson.JsonArray
import weather.pusher.models.OwmRsp
import weather.pusher.models.OwmRspWeather
import weather.pusher.models.OwmWeatherMain
import weather.pusher.models.WeatherData

/* @desc utility used throughout the application for things like parsing , intents */
class FilterUtil {
    fun filterForecastOnSliderResults(user_min: Int, user_max: Int, user_data: OwmRsp?): JsonArray {
        val jsoncollection = JsonArray()
        println(user_data)
        //for each element in the json response get the weather list data element at i
        //, each day that matches the user min user max puts the data into a separate json object
        for(i in 0 until user_data?.list?.size()!!){
            val weather: OwmRspWeather
                    = Gson().fromJson(user_data.list.get(i),
                OwmRspWeather::class.java)
            val maininfo: OwmWeatherMain
                    = Gson().fromJson(weather.main,
                OwmWeatherMain::class.java)
            if(maininfo.temp_min >= user_min && maininfo.temp_max <= user_max){
                val client_date: String = weather.dt_txt.substringBefore(" ")
                val client_time: String = weather.dt_txt.substringAfter(" ")
                val client_min: Double = maininfo.temp_min
                val client_max: Double = maininfo.temp_max
                jsoncollection.add(Gson().toJson(
                    WeatherData(client_date, client_time, client_min, client_max)
                ))
            }
        }
        return jsoncollection
    }
}