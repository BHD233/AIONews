package com.example.aionews

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import java.io.ByteArrayOutputStream


class NewsDBHandle(var context: Context, var factory: SQLiteDatabase.CursorFactory?, var name: String? = "news.db", var version: Int = 1)
    : SQLiteOpenHelper(context, name, factory, version){

    var tableName: String = "NEWS"
    var columnID: String = "ID"
    var columnProvider: String = "PROVIDER"
    var columnTitle: String = "TITLE"
    var columnLink: String = "LINK"
    var columnImgSrc: String = "IMG"
    var columnDatePub: String = "DATEPUB"
    var columnCategory: String = "CATEGORY"
    var columnImgRaw: String = "IMGRAW"

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("DROP TABLE IF EXISTS $tableName")

        var query: String = "CREATE TABLE $tableName (" +
                            "$columnID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "$columnProvider TEXT, " +
                "$columnTitle TEXT, " +
                "$columnLink TEXT UNIQUE, " +
                "$columnImgSrc TEXT, " +
                "$columnCategory TEXT, " +
                "$columnDatePub TEXT);"

        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $tableName")

        onCreate(db)
    }

    fun deleteDatabase(){
        var db = writableDatabase

        db!!.execSQL("DROP TABLE IF EXISTS $tableName")

        var query: String = "CREATE TABLE $tableName (" +
                "$columnID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$columnProvider TEXT, " +
                "$columnTitle TEXT, " +
                "$columnLink TEXT UNIQUE, " +
                "$columnImgSrc TEXT, " +
                "$columnCategory TEXT, " +
                "$columnDatePub TEXT);"

        db!!.execSQL(query)

        Log.w("BHD", "call delete")
        Log.w("BHD", "new size: ${sizeOfDB()}")

        db.close()
    }

    fun genDateInt(s: String): Int{
        var idOut: Int = 0

        var i: Int = 0
        //get day
        while(s[i] < '0' || s[i] > '9'){
            i++
        }

        return idOut
    }

    fun addNews(newsSummary: NewsSummary){
        var runnable = Runnable{
            var value = ContentValues()

            val dateInt: Int = genDateInt(newsSummary.datePub)

            value.put(columnProvider, newsSummary.provider)
            value.put(columnTitle, newsSummary.title)
            value.put(columnLink, newsSummary.link)
            value.put(columnImgSrc, newsSummary.imgSrc)
            value.put(columnCategory, newsSummary.category)
            value.put(columnDatePub, newsSummary.datePub)

            var db = writableDatabase
            db.insert(tableName, null, value)

            Log.w("BHD", "Add")
        }

        var thread = Thread(runnable)
        thread.start()
    }

    fun sizeOfDB(category: String = ""): Int{
        var db = writableDatabase

        var query: String

        if (category == "") {
            query = "SELECT * FROM $tableName;"
        } else {
            query = "SELECT * FROM $tableName WHERE $columnCategory = '$category';"
        }
        var c: Cursor

        c = db.rawQuery(query, null)

        var size: Int = c.count

        c.close()

        return size
    }

    fun removeNews(id: Int){
        var db = writableDatabase

        var query: String = "DELETE FROM $tableName WHERE $columnID = $id;"

        db.execSQL(query)
    }

    fun getTopNews(category: String = "", num: Int = 50, maxDate: String = "z"): ArrayList<NewsSummary>{
        var outputList: MutableList<NewsSummary> = arrayListOf()

        var db = writableDatabase

        var query = ""

        if (category == "") {
            query =
                "SELECT * FROM $tableName WHERE $columnDatePub < \'$maxDate\' ORDER BY $columnDatePub DESC LiMIT $num"
        } else {
            query =
                "SELECT * FROM $tableName WHERE $columnDatePub < \'$maxDate\' AND $columnCategory = '$category'" +
                        " ORDER BY $columnDatePub DESC LiMIT $num"
        }

        var c: Cursor

        c = db.rawQuery(query, null)

        c.moveToFirst()

        while (!c.isAfterLast) {
            var curNewsSummary = NewsSummary()

            curNewsSummary.provider = c.getString(c.getColumnIndex(columnProvider))
            curNewsSummary.title = c.getString(c.getColumnIndex(columnTitle))
            curNewsSummary.link = c.getString(c.getColumnIndex(columnLink))
            curNewsSummary.datePub = c.getString(c.getColumnIndex(columnDatePub))
            curNewsSummary.imgSrc = c.getString(c.getColumnIndex(columnImgSrc))

            curNewsSummary.revertDate()

            outputList.add(curNewsSummary)

            c.moveToNext()
        }
        
        c.close()

        Log.w("BHD", "Get 50 is: ${outputList.size},  minID is: $maxDate")

        return outputList as ArrayList<NewsSummary>
    }

    fun getNews(id: Int): NewsSummary{
        var newsSummary = NewsSummary()

        var db = writableDatabase

        var query: String = "SELECT * FROM $tableName WHERE 1"

        var c: Cursor = db.rawQuery(query, null)

        c.moveToFirst()

        newsSummary.provider = c.getString(c.getColumnIndex(columnProvider))
        newsSummary.title = c.getString(c.getColumnIndex(columnTitle))
        newsSummary.link = c.getString(c.getColumnIndex(columnLink))
        newsSummary.datePub = c.getString(c.getColumnIndex(columnDatePub))
        newsSummary.imgSrc = c.getString(c.getColumnIndex(columnImgSrc))

        return newsSummary
    }

}