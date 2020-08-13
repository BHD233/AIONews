package com.example.aionews

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.nostra13.universalimageloader.utils.IoUtils
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check if sign in or not
        val sharedPref = getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE) ?: return
        val username: String? = sharedPref.getString("username", "")

        if (username != "" && username != null){
            var intent = Intent(this, NewsOverviewActivity::class.java)
            intent.putExtra("username", username)

            startActivity(intent)
        }
    }

    fun onLogInButtonCLicked(view: View) {
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun onGuestButtonClicked(view: View) {
        var intent = Intent(this, ChooseNewsProvider::class.java)

        startActivity(intent)
    }

}