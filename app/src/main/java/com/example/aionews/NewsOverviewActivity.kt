package com.example.aionews

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.aionews.ui.home.HomeFragment
import com.google.android.material.navigation.NavigationView
import kotlin.concurrent.fixedRateTimer

class NewsOverviewActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_overview)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.navChangeFavorite, R.id.navLogOut, R.id.nav_hot,
                R.id.nav_the_gioi, R.id.nav_cong_nghe, R.id.nav_bds
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //===========MY==============//

        //get username to say hello
        var username: String? = ""
        sharedPref = getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE)
        username = sharedPref!!.getString("username", "Guest")

        val isInit = sharedPref!!.getBoolean("isInit", false)

        val editor = sharedPref!!.edit()
        editor.putString("category", "")

        Toast.makeText(this, "Welcome $username", Toast.LENGTH_LONG).show()

        //add username to drawer
        var viewNavView: View = navView.getHeaderView(0)
        var usernameTextView: TextView = viewNavView.findViewById(R.id.drawerLayoutUsername)
        usernameTextView.text = username

        //get rss
        if (!isInit) {
            //Toast.makeText(this, "Start service", Toast.LENGTH_LONG).show()
            var newIntent = Intent(this, GetNewsOverviewService::class.java)
            startService(newIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.news_overview, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 1) {
            var fragment =
                supportFragmentManager.fragments[supportFragmentManager.fragments.size - 1]
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    fun onChangeProicderClicked(item: MenuItem) {
        var intent = Intent(this, ChooseNewsProvider::class.java)

        startActivity(intent)
    }

    fun onLogoutClicked(item: MenuItem) {
        val sharedPref = this?.getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("username", "")

            commit()
        }
        var intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
    }
}