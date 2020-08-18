package com.example.aionews

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import androidx.work.*
import java.util.concurrent.TimeUnit

class GetNewsOverviewService : Service(){
    var context: Context? = null
    var listRssID: ArrayList<Int> = arrayListOf()
    var listProvider: ArrayList<String> = arrayListOf()
    var newsProvider = NewsProvider()
    var dbHandle: NewsDBHandle? = null

    var waitTime: Long = 60000 * 30
    var maxLenghtText: Int = 75

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        listRssID.clear()
        listProvider.clear()

        //get all rss option
        val sharedPref = getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE)
        var size :Int = sharedPref.getInt("rssSize", 0)

        val editor = sharedPref.edit()
        editor.putBoolean("isInit", true)
        editor.commit()

        for(i in 0 until size){
            var id: Int = sharedPref.getInt("id$i", - 1)

            if (id != -1 && id < newsProvider.getListProvider().size) {
                listProvider.add(newsProvider.getListProvider()[id].title)
                listRssID.add(newsProvider.getListProvider()[id].ID)
            }
        }

        //create database handle
        dbHandle = NewsDBHandle(this, null)

        context = this

        startCollectNews()

        return Service.START_STICKY
    }

    fun getListRss(fileName: String): ArrayList<Rss>{
        var rssOut: MutableList<Rss> = arrayListOf()

        var text = application.assets.open(fileName).bufferedReader().use{
            it.readText()
        }

        var token = text.split(" > ")

        var i: Int = 0
        while(i < token.size){
            var curRss = Rss()
            curRss.rss = token[i++]
            curRss.category = token[i++]

            rssOut.add(curRss)
        }

        return rssOut as ArrayList<Rss>
    }

    fun startCollectNews(){
        for(i in listRssID.indices) {
            while (context == null || dbHandle == null) {
                //wait until context have value
            }

            var title = listProvider[i]
            val fileName = newsProvider.getListProvider()[listRssID[i]].rssFile
            var listRss = getListRss(fileName)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            var uploadWorkRequest =
                PeriodicWorkRequestBuilder<CollectionNewsWork>(30, TimeUnit.MINUTES)
                    .setInputData(getDataForWorker(listRss, title))
                    .setConstraints(constraints)
                    .addTag("Get news service")
                    .build()

            WorkManager.getInstance(this).enqueue(uploadWorkRequest)
        }
    }

    fun getDataForWorker(listRss: ArrayList<Rss>, title: String): Data{
        val dataBuilder = Data.Builder()

        dataBuilder.putString("title", title)
        dataBuilder.putInt("size", listRss.size)

        for (i in 0 until listRss.size){
            dataBuilder.putString("rss$i", listRss[i].rss)
            dataBuilder.putString("category$i", listRss[i].category)
        }

        return dataBuilder.build()
    }

}