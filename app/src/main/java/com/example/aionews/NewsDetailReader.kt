package com.example.aionews.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.aionews.R
import kotlinx.android.synthetic.main.fragment_detail_view.*

class NewsDetailReader : Fragment() {

    var detailViewWebView: WebView? = null
    var thisContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_detail_view, container, false)

        detailViewWebView = root.findViewById(R.id.detailViewWebView)

        val sharedPref = thisContext!!.getSharedPreferences("DucSharedPre", Context.MODE_PRIVATE)
        val url: String? = sharedPref.getString("link", "")

        if (url != ""){
            while(thisContext == null){
                //wait
            }

            detailViewWebView!!.loadUrl(url!!)
        }

        Log.w("BHD", "start news reader")

        return root
    }

    override fun onAttach(context: Context) {
        thisContext = context

        super.onAttach(context)
    }
}