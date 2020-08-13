package com.example.aionews

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onSignUpButtonClicked(view: View){
        var loginUserName: EditText = findViewById(R.id.loginUsername)

        var username: String = loginUserName.text.toString()

        val sharedPref = this?.getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("username", username)

            commit()
        }

        var intent = Intent(this, ChooseNewsProvider::class.java)

        var newsProvider = NewsProvider()
        var listProvider = newsProvider.getListProvider()

        intent.putExtra("n", listProvider.size)
        for (i in listProvider.indices){
            intent.putExtra("srr$i", listProvider.get(i).ID)
        }

        startActivity(intent)
    }

    fun onSigninButtonClicked(view: View){
        var loginUserName: EditText = findViewById(R.id.loginUsername)
        var loginPass: EditText = findViewById(R.id.loginPass)

        var username: String = loginUserName.text.toString()
        var pass: String = loginPass.text.toString()

        //check pass here
        if (username == "Duc" && pass == "Duc"){
            //start new activity and save login state
            val sharedPref = this?.getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString("username", username)

                commit()
            }

            var intent = Intent(this, ChooseNewsProvider::class.java)

            var newsProvider = NewsProvider()
            var listProvider = newsProvider.getListProvider()

            intent.putExtra("n", listProvider.size)
            for (i in listProvider.indices){
                intent.putExtra("srr$i", listProvider.get(i).ID)
            }

            startActivity(intent)
        }
    }
}