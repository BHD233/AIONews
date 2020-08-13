package com.example.aionews

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.aionews.ui.slideshow.NewsDetailReader

class TheGioiFragment : Fragment() {

    var adapter: NewsOverviewAdapter? = null
    var listView: ListView? = null
    var listNewsSummary: ArrayList<NewsSummary> = arrayListOf()
    var thisContext: Context? = null
    var refreshLayout: SwipeRefreshLayout? = null
    var shouldStartAnimation: Boolean = true

    var dbHandle: NewsDBHandle? = null

    var isFirstTime: Boolean = true
    var waitTime: Long = 4000       //in milisec

    var isOnTop: Boolean = true

    var maxDate = "z"

    var numRequestGetLastSent: Int = 0
    var maxNumRequsetGetLast: Int = 5

    var category = "Thế giới"

    private val myhandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            //load it
            while(adapter == null || listView == null || thisContext == null){
                //wait
            }

            if (msg.what == 0) {
                val state = listView!!.onSaveInstanceState()

                adapter!!.addNewsSummary(listNewsSummary)

                adapter!!.notifyDataSetChanged()

                listView!!.adapter = adapter

                listView!!.onRestoreInstanceState(state);
            }

            if (msg.what == 1) {
                refreshLayout!!.isRefreshing = true
            }

            if (msg.what == 2) {
                refreshLayout!!.isRefreshing = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        listView = root.findViewById(R.id.homeListView)

        listView!!.setOnItemClickListener{ parent, view, position, id ->
            val url: String = listNewsSummary[position].link

            val sharedPref = thisContext!!.getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("link", url)
            editor.commit()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, NewsDetailReader())
                .commit()

        }

        listView!!.setOnScrollListener(object: AbsListView.OnScrollListener{
            private var currentVisibleItemCount = 0
            private var currentScrollState = 0
            private var currentFirstVisibleItem = 0
            private var totalItem = 0

            override fun onScroll( view: AbsListView, firstVisibleItem: Int,
                                   visibleItemCount: Int, totalItemCount: Int) {
                if (listView!!.getLastVisiblePosition() == (adapter!!.count- 1)){
                    //end of list
                    numRequestGetLastSent++
                    shouldStartAnimation = false
                    if (listNewsSummary.size < dbHandle!!.sizeOfDB(category!!) || numRequestGetLastSent < maxNumRequsetGetLast) {
                        startGetNewsFromDBService(thisContext!!, 20)
                    }
                } else{
                    numRequestGetLastSent = 0
                }
            }

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                currentScrollState = scrollState
            }
        })

        refreshLayout = root.findViewById(R.id.newsOverviewRefreshLayout)
        refreshLayout!!.setOnRefreshListener {
            clearOldData()
            startGetNewsFromDBService(thisContext!!)
        }

        return root
    }

    fun clearOldData(){
        maxDate = "z"
        listNewsSummary.clear()
    }

    override fun onAttach(context: Context) {
        clearOldData()

        adapter = NewsOverviewAdapter(context)

        thisContext = context

        startGetNewsFromDBService(context)

        super.onAttach(context)
    }

    fun startGetNewsFromDBService(context: Context, num: Int = 20){
        dbHandle = NewsDBHandle(context, null)

        var runable: Runnable = object : Runnable {
            override fun run() {

                //if show all => not get
                var curListNewsSummary: ArrayList<NewsSummary> = arrayListOf()

                while (refreshLayout == null) {
                    //wait to get refresh layout
                }
                //start animation
                myhandler.sendEmptyMessage(1)

                //wait for about time to ensure all rss is collected (first time)
                if (isFirstTime) {
                    var startTime = System.currentTimeMillis()

                    while (System.currentTimeMillis() - startTime < waitTime) {
                        //wait
                    }

                    isFirstTime = false
                }

                //get news
                var startTime = System.currentTimeMillis()
                do {
                    var endTime = System.currentTimeMillis()
                    if (endTime - startTime > waitTime) {
                        break
                    }

                    curListNewsSummary = dbHandle!!.getTopNews(category, num, maxDate)

                } while (curListNewsSummary.size < 0)

                //myhandler.sendEmptyMessage(0)

                //load it
                while (adapter == null || listView == null || thisContext == null) {
                    //wait
                }

                //merge listNewsSummary

                curListNewsSummary.forEach {
                    if (listNewsSummary.indexOf(it) < 0) {
                        listNewsSummary.add(it)
                    }
                }

                //listNewsSummary.addAll(curListNewsSummary)
                //start load
                if (isOnTop) {
                    myhandler.sendEmptyMessage(0)
                }
                //update maxDate
                if (listNewsSummary.size > 0) {
                    maxDate = listNewsSummary.last().revertDate()
                    listNewsSummary.last().revertDate()

                }
                //set refreshing is false
                myhandler.sendEmptyMessage(2)

                //adapter = NewsOverviewAdapter(thisContext!!, listNewsSummary)
                //listVIew!!.adapter = adapter
            }
        }

        var thread = Thread(runable)
        thread.start()
    }
}
