package weather.pusher.services

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import weather.pusher.models.OwmRsp

interface WeatherService {

    /* @API_FORMAT "/...") */

    @GET("data/2.5/forecast")
    fun getWeatherData(
        @Query("q" , encoded = true) city: String
        , @Query("appid") appid: String
        , @Query("units") units: String
    ) : Call<OwmRsp>
}