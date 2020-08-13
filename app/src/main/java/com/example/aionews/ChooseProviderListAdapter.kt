package com.example.aionews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class ChooseProviderListAdapter(val context: Context, var dataProvider: List<NewsProvider>) : BaseAdapter(){

    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.asset_choose_news_provider, parent, false)

        val textView: TextView = view.findViewById(R.id.chooseProviderText)

        textView.text = dataProvider[position].title

        val checkBox: CheckBox = view.findViewById(R.id.chooseProviderCheckBox)

        checkBox.isChecked = dataProvider[position].isCheck

        return view
    }

    override fun getItem(position: Int): Any {
        return dataProvider[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataProvider.size
    }

}