package com.example.test0

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AdapterPub (val context: Context, val listPub: ArrayList<ClassPub?>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_public, null)
        val pubId = view.findViewById<TextView>(R.id.pubId_tv)
        val pubName = view.findViewById<TextView>(R.id.pubName_tv)
        val pubRelations = view.findViewById<TextView>(R.id.pubRelations_tv)
        val pubAddress = view.findViewById<TextView>(R.id.pubAddress_tv)
        val pubBirth = view.findViewById<TextView>(R.id.pubBirth_tv)
        val pubSsn = view.findViewById<TextView>(R.id.pubSsn_tv)
        val pubSex = view.findViewById<TextView>(R.id.pubSex_tv)

        val account = listPub[position]

        pubId.text = account?.id
        pubName.text = "이름: " + account?.name
        pubRelations.text = "관계: " + account?.relations
        pubAddress.text = "주소: " + account?.address
        pubBirth.text = "생일: " + account?.birth
        pubSsn.text = "SSN: " + account?.ssn
        pubSex.text = "성별: " + account?.sex

        return view
    }

    override fun getItem(position: Int): String? {
        return listPub[position]?.id
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listPub.size
    }
}