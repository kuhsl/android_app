package com.example.test0

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class AdapterMed (val context: Context, val listMed: ArrayList<ClassMed?>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_medical, null)
        val UserId = view.findViewById<TextView>(R.id.medType_tv)
        val MedTime = view.findViewById<TextView>(R.id.medTime_tv)
        val MedImage = view.findViewById<TextView>(R.id.medImage_tv)

        val account = listMed[position]

        UserId.text = account?.user_id
        MedTime.text = account?.date_time
        MedImage.text = account?.image_path

        return view
    }

    override fun getItem(position: Int): ClassMed? {
        return listMed[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listMed.size
    }
}