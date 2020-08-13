package com.example.aionews

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import java.io.UnsupportedEncodingException

class CollectionNewsWork(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    val maxLenghtText: Int = 75
    var listRss = arrayListOf<Rss>()
    val context = appContext
    var provider: String? = ""
    val listMonth: ArrayList<String> = arrayListOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var dbHandle: NewsDBHandle? = null


    override fun doWork(): Result {
        //get all rss
        try {
            provider = inputData.getString("title")
            val size = inputData.getInt("size", 0)

            for (i in 0 until size){
                val rssString = inputData.getString("rss$i")
                val category = inputData.getString("category$i")

                if (rssString != null && category != null){
                    listRss.add(Rss(rssString, category))
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

        dbHandle = NewsDBHandle(context, null)

        startCollect()

        return Result.success()
    }

    fun startCollect(){
        listRss.forEach{
            Log.w("BHD1", "get from ${it.category}")

            var queue = Volley.newRequestQueue(context)
            var url: String = it.rss
            var title: String = provider!!
            var category: String = it.category


            // Request a string response from the provided URL
            val stringRequest = CollectionNewsWork.VolleyUTF8EncodingStringRequest(
                Request.Method.GET,
                url,
                Response.Listener { response ->
                    addRssToDB(response, title, category)
                },
                Response.ErrorListener { error ->
                    Log.w("BHD", error)
                })

            // Add the volley string request to the request queue
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)

            queue.add(stringRequest)
        }
    }

    fun getFormatDate(s: String): String{
        var outPutString: String = ""

        if (s.indexOf("/") > 0){
            var first = s.indexOf("/", 0)
            var sec = s.indexOf("/", first + 1)

            var a = s.substring(0, first)
            var b = s.substring(first + 1, sec)
            var c = s.substring(sec + 1, s.length)

            if (a.length < 2){
                a = "0$a"
            }

            return "$b/$a/$c"
        }

        var subString: String = s.substring(s.indexOf(", ") + 2, s.length)

        outPutString += subString.substring(0, 2)

        outPutString += "/"

        var monthText = subString.substring(3, 6)

        outPutString += (listMonth.indexOf(monthText) + 1).toString()

        outPutString += "/"

        outPutString += subString.substring(7, subString.length)

        return outPutString
    }

    fun cleanString(s: String): String{
        var startString = "CDATA["
        var endString = "]]>"

        var sOut: String = ""

        var start: Int = s.indexOf(startString, 0)
        var end: Int = s.indexOf(endString, 0)

        if (start >= 0){
            sOut = s.substring(start + startString.length, end)
        } else {
            sOut = s
        }

        return sOut
    }

    fun fitTitleText(s: String): String{
        var sOut: String = s

        if (s.length > maxLenghtText){
            sOut = s.substring(0, maxLenghtText - 4)
            sOut += " ..."
        }

        return sOut
    }

    fun cleanDate(s: String): String{
        var sOut = s.substring(0, s.indexOf(":", 0) + "00:00".length)

        sOut = getFormatDate(sOut)

        var listText = sOut.split(" ")

        sOut = listText[0] + " "
        if (listText[1].length < "00:00:0".length){
            sOut = sOut + "0" + listText[1]
        } else{
            sOut += listText[1]
        }

        return sOut
    }

    fun addRssToDB(s: String, provider: String, category: String){
        var cur: Int = 1
        var pos: Int
        var curString: String
        var curNewsSummary: NewsSummary
        var next: Int = 0

        while(cur < s.length && cur > 0){
            curNewsSummary = NewsSummary(provider)
            curNewsSummary.category = category

            cur = s.indexOf("<item>", cur)
            next = s.indexOf("<item>", cur + 1)

            if (cur < 0){
                break
            }

            //position of title
            pos = s.indexOf("title", cur)
            pos = s.indexOf(">", pos) + 1
            curString = s.substring(pos, s.indexOf("</title>", pos))
            curString = cleanString(curString)
            curString = fitTitleText(curString)

            curNewsSummary.title = curString

            //position of img
            //NOTE THAT IMG IS OPTIONAL IN SOME RSS
            pos = s.indexOf("<img", cur)
            pos = s.indexOf("src=\"", pos) + 5
            if (pos < 0 || pos > next){
                curString = ""
            } else {
                curString = s.substring(pos, s.indexOf("\"", pos))
            }
            curNewsSummary.imgSrc = curString

            //position of datePub
            pos = s.indexOf("<pubDate>", cur) + 9
            curString = s.substring(pos, s.indexOf("</pubDate>", pos))
            curString = cleanString(curString)
            curString = cleanDate(curString)

            curNewsSummary.datePub = curString
            curNewsSummary.revertDate()

            //position of link
            pos = s.indexOf("<link>", cur) + 6
            var next = s.indexOf("</link>", pos)
            var next2 = s.indexOf("]]>", pos)

            if (next2 > 0 && next2 < next){
                next = next2
            }

            curString = s.substring(pos, next)

            var listText = curString.split(":")
            curString = "https:" + listText[1]

            curString = cleanString(curString)

            curNewsSummary.link = curString

            dbHandle!!.addNews(curNewsSummary)

            cur++
        }
    }


    class VolleyUTF8EncodingStringRequest(
        method: Int, url: String, private val mListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) : Request<String>(method, url, errorListener) {

        override fun deliverResponse(response: String) {
            mListener.onResponse(response)
        }

        override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
            var parsed = ""

            val encoding = charset(HttpHeaderParser.parseCharset(response.headers))

            try {
                parsed = String(response.data, encoding)
                val bytes = parsed.toByteArray(encoding)
                parsed = String(bytes, charset("UTF-8"))

                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
            } catch (e: UnsupportedEncodingException) {
                return Response.error(ParseError(e))
            }
        }
    }
}