package com.sleepygloa.weather1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1000
    private val APP_ID = "73317eb1c19145de29ea54fca800ffd4"
    private val URL = "https://openweathermap.org/current"
    private val STETHO_URL = "chrome://inspect/#devices"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("START", "MainActivity onCreate")
        checkPreviousLogin()
    }

    private fun checkPreviousLogin(){
        var user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            showLoginWindow()
        }
        else {
            moveToOpenWeatherActivity()
        }
    }

    private fun moveToOpenWeatherActivity(){
        startActivity(Intent(this, OpenWeatherActivity::class.java))
    }
    private fun showLoginWindow(){
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            //AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            //AuthUI.IdpConfig.FacebookBuilder().build(),
            //AuthUI.IdpConfig.TwitterBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.main_icon)
                .build(),
            RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                startActivity(Intent(this, OpenWeatherActivity::class.java))
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this,"로그인 실패, 로그인을 다시 시도해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }
}