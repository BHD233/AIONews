package com.example.aionews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

class NewsOverviewAdapter(val context: Context, var listNewsSummary: ArrayList<NewsSummary> = arrayListOf()) : BaseAdapter(){

    val imageLoader: ImageLoader = ImageLoader.getInstance()
    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.fragment_card_view, parent, false)

        var imageView: ImageView = view.findViewById(R.id.cardViewImg)
        var providerView: TextView = view.findViewById(R.id.cardViewNewsProvider)
        var titleView: TextView = view.findViewById(R.id.cardViewTitle)
        var dateView: TextView = view.findViewById(R.id.cardViewTime)

        if (!imageLoader.isInited) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        imageLoader.displayImage(listNewsSummary[position].imgSrc, imageView)

        providerView.text = listNewsSummary[position].provider
        titleView.text = listNewsSummary[position].title
        dateView.text = listNewsSummary[position].datePub

        return view
    }

    override fun getItem(position: Int): Any {
        return listNewsSummary[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listNewsSummary.size
    }

    fun addNewsSummary(listSummaty: ArrayList<NewsSummary>){
        listNewsSummary = listSummaty
    }
}