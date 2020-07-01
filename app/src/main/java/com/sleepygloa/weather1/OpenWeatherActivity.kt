package com.sleepygloa.weather1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.UserDictionary.Words.APP_ID
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_open_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherActivity : AppCompatActivity(), LocationListener{

    private val PERMISSION_REQUEST_CODE = 2000
    private val APP_ID = "73317eb1c19145de29ea54fca800ffd4"
    private val UNITS = "metric"
    private val LANGUAGE = "kr"
    private lateinit var backPressHolder: OnBackPressHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_weather)

        backPressHolder = OnBackPressHolder(this@OpenWeatherActivity)
        getLocationInfo();

        account_setting.setOnClickListener {
            //onBackPressed()
            startActivity(Intent(this, AccountSettingActivity::class.java))
        }
    }



    private fun drawCurrentWeather(currentWeather: TotalWeather){
        with(currentWeather){

            this.weatherList?.getOrNull(0)?.let{
                it.icon?.let{
                    val glide = Glide.with(this@OpenWeatherActivity)
                    glide.load(Uri.parse("https://openweathermap.org/img/w/"+it+".png"))
                        .into(current_icon)
                }

                it.main?.let{ current_main.text = it }
                it.description?.let{ current_description.text = it }


            }

            this.main?.temp?.let{ current_max.text = String.format("%.1f", it) }
            this.main?.tempMax?.let{ current_min.text = String.format("%.1f", it) }
            this.main?.tempMin?.let{ current_now.text = String.format("%.1f", it) }

            loading_view.visibility = View.GONE
            weather_view.visibility = View.VISIBLE

        }
    }

    private fun getLocationInfo(){
        Log.d("TEST" , "TEST")
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }else{
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location != null){
                val latitude = location.latitude
                val longitude = location.longitude
                Log.d("TEST", "1 lotitue : "+ latitude)
                requestWeatherInfoOfLocation(latitude = latitude,longitude = longitude)
            }else{
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000L,
                    0F,
                    this
                )
                locationManager.removeUpdates(this)
            }
        }
    }

    private fun requestWeatherInfoOfLocation(latitude: Double, longitude: Double){
        (application as WeatherApplication)
            .requestService()
            ?.getWeatherInfoOfCoordinates(
                latitude = latitude,
                longitude =  longitude,
                appID = APP_ID,
                units = UNITS,
                language = LANGUAGE
            )
            ?.enqueue(object: Callback<TotalWeather>{
                override fun onFailure(call: Call<TotalWeather>, t: Throwable) {
                    loading_text.text = "로딩 실패"
                }

                override fun onResponse(
                    call: Call<TotalWeather>,
                    response: Response<TotalWeather>
                ) {
                    if(response.isSuccessful){
                        val totalWeather = response.body()
                        totalWeather?.let {
                            drawCurrentWeather(it)
                        }
                    }else{
                        loading_text.text = "로딩 실패"
                    }

                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK) getLocationInfo()
        }
    }

    override fun onLocationChanged(location: Location?) {
        var latitude = location?.latitude
        var longitude = location?.longitude
        Log.d("TEST", "2 : " + longitude)
        if(latitude != null && longitude !== null){
            requestWeatherInfoOfLocation(latitude = latitude,longitude = longitude)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }

    /********************************************************
     * 뒤로가기 버튼 눌렀을때.
     ********************************************************/
    override fun onBackPressed() {
        backPressHolder.onBackPressed()
    }
    inner class OnBackPressHolder(val activity: Activity){
        private var backPressHolder: Long = 0
        fun onBackPressed(){
            if(System.currentTimeMillis() > backPressHolder * 2000){
                backPressHolder = System.currentTimeMillis()
                showBackToast()
                return
            }
            if(System.currentTimeMillis() <= backPressHolder * 2000){
                finishAffinity()
            }
        }
        fun showBackToast(){
            Toast.makeText(this@OpenWeatherActivity, "한번더 누르시면 종료합니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun requestCurrentWeather(){
        (application as WeatherApplication)
            .requestService()
            ?.getWeatherInfoOfLocation("London", "73317eb1c19145de29ea54fca800ffd4")
            ?.enqueue(object: Callback<TotalWeather>{
                override fun onFailure(call: Call<TotalWeather>, t: Throwable) {
                }

                override fun onResponse(call: Call<TotalWeather>, response: Response<TotalWeather>) {
                    //Log.d("requestt", "response:"+response.body())
                    var totalWeather = response.body()
                    Log.d("TEST", "MAIN : :"+totalWeather?.main?.tempMax)
                }
            })
    }

    /**************************************************
     * application 단의 retrofit
     * ***********************************************/
//    private fun setupRetrofit(){
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.openweathermap.org")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//
//        var service = retrofit.create(Service::class.java)
//
//        service.getWeatherInfoOfLocation("London", "73317eb1c19145de29ea54fca800ffd4")
//            .enqueue(object: Callback<JsonObject>{
//                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                }
//
//                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    Log.d("abc", "response"+response.body())
//                }
//            })
//    }

}