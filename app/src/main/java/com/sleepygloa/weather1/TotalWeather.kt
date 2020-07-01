package com.sleepygloa.weather1

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TotalWeather(
//    var base : String? = null,
//    var clouds: Clouds? = null,
    var main: Main? = null,
    @SerializedName("weather")
    var weatherList: ArrayList<Weather>? = null
):Serializable

//class Clouds(
//    var all : Int? = null
//)

class Weather(
    var description: String? = null,
    var icon: String? = null,
    var main: String? = null

):Serializable

class Main(
    var humidity : Int? = null,
    var pressure : Int? = null,
    var temp : Float? = null,
    @SerializedName("temp_max")
    var tempMax : Float? = null,
    @SerializedName("temp_min")
    var tempMin: Float? = null

):Serializable


/*
{
  "coord": {
    "lon": -0.13,
    "lat": 51.51
  },
  "weather": [
    {
      "id": 803,
      "main": "Clouds",
      "description": "broken clouds",
      "icon": "04d"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 287.37,
    "feels_like": 284.53,
    "temp_min": 285.93,
    "temp_max": 288.15,
    "pressure": 1004,
    "humidity": 82
  },
  "visibility": 10000,
  "wind": {
    "speed": 4.6,
    "deg": 230
  },
  "clouds": {
    "all": 51
  },
  "dt": 1593580353,
  "sys": {
    "type": 1,
    "id": 1414,
    "country": "GB",
    "sunrise": 1593575265,
    "sunset": 1593634849
  },
  "timezone": 3600,
  "id": 2643743,
  "name": "London",
  "cod": 200
}

 */