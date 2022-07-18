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
import org.w3c.dom.Text

class AdapterMed (val context: Context, val listMed: ArrayList<ClassMed?>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_medical, null)
        val medId = view.findViewById<TextView>(R.id.medType_tv)
        val medName = view.findViewById<TextView>(R.id.medName_tv)
        val medSex = view.findViewById<TextView>(R.id.medSex_tv)
        val medSsn = view.findViewById<TextView>(R.id.medSsn_tv)
        val medTime = view.findViewById<TextView>(R.id.medTime_tv)
        val medRecovered = view.findViewById<TextView>(R.id.medRecovered_tv)
        val medDiseaseName = view.findViewById<TextView>(R.id.medDiseaseName_tv)
        val medDiseaseNum = view.findViewById<TextView>(R.id.medDiseaseNum_tv)
        val medImage = view.findViewById<TextView>(R.id.medImage_tv)

        val account = listMed[position]

        medId.text = account?.user_id
        medName.text = account?.name
        medSex.text = account?.sex
        medSsn.text = account?.ssn
        medTime.text = account?.date_time
        medRecovered.text = account?.recovered
        medDiseaseName.text = account?.disease_name
        medDiseaseNum.text = account?.disease_num
        medImage.text = account?.image_path

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