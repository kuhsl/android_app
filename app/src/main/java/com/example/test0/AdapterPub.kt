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
        val PubId = view.findViewById<TextView>(R.id.pubId_tv)
        val PubName = view.findViewById<TextView>(R.id.pubName_tv)
        val PubRelation = view.findViewById<TextView>(R.id.pubRelation_tv)
        val PubBirth = view.findViewById<TextView>(R.id.pubBirth_tv)
        val PubSsn = view.findViewById<TextView>(R.id.pubSsn_tv)
        val PubSex = view.findViewById<TextView>(R.id.pubSex_tv)

        val account = listPub[position]

        PubId.text = account?.user_id
        PubName.text = "이름: " + account?.name
        PubRelation.text = "관계: " + account?.relation
        PubBirth.text = "생일: " + account?.birth
        PubSsn.text = "SSN: " + account?.ssn
        PubSex.text = "성별: " + account?.sex

        return view
    }

    override fun getItem(position: Int): String? {
        return listPub[position]?.user_id
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listPub.size
    }
}