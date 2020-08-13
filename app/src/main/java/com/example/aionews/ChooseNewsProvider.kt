package com.example.aionews

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ListView
import androidx.core.view.get
import androidx.core.view.size
import androidx.work.WorkManager

class ChooseNewsProvider : AppCompatActivity() {
    var newsProvider = NewsProvider()
    var username: String? = ""
    var thisContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        var n: Int = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_news_provider)

        thisContext = this

        //clear old work
        WorkManager.getInstance().cancelAllWork()

        //get old favorite and user name
        var bundle: Bundle? = intent.extras

        if (bundle != null) {

            n = bundle!!.getInt("n")

            if (n > 0) {      //not guest
                for (newsRss in newsProvider.getListProvider().iterator()) {
                    newsRss.isCheck = true
                }
            }
        }

        getListView()
    }

    fun getListView(){
        val listView: ListView = findViewById(R.id.chooseProviderListview)

        val adapter = ChooseProviderListAdapter(this, newsProvider.getListProvider())

        listView.adapter = adapter

        //if click on item => change check box state (to know what check box checked
        listView.setOnItemClickListener{ parent, view, position, id ->
            val checkBox: CheckBox = view.findViewById(R.id.chooseProviderCheckBox)

            checkBox.isChecked = !checkBox.isChecked

            //change state of is check in NewsProvider list
            newsProvider.getListProvider().get(position).isCheck = !newsProvider.getListProvider().get(position).isCheck
        }
    }

    fun onSubmitButtonClicked(view: View) {

        val sharedPref = this?.getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            var i: Int = 0

            //delete old database
            var db = NewsDBHandle(thisContext!!, null)
            db.deleteDatabase()

            for (newsRss in newsProvider.getListProvider().iterator()){
                if (newsRss.isCheck){
                    putInt("id$i", newsRss.ID)

                    i++
                }
            }

            putInt("rssSize", i)

            commit()
        }

        val intent = Intent(this, NewsOverviewActivity::class.java)

        startActivity(intent)
    }
}