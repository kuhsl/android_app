package com.example.test0

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AdapterFin (val context: Context, val listFin: ArrayList<ClassFin?>, val listTx: String) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_financial, null)
        val UserId = view.findViewById<TextView>(R.id.user_id_tv)
        val AccountNo = view.findViewById<TextView>(R.id.accountNo_tv)
        val Balance = view.findViewById<TextView>(R.id.balance_tv)
        val Transactions = view.findViewById<TextView>(R.id.transaction_tv)

        val account = listFin[position]

        UserId.text = account?.user_id
        AccountNo.text = "Account Number: "+account?.account
        Balance.text = "Balance: "+account?.balance
        Transactions.text = listTx

        return view
    }

    override fun getItem(position: Int): ClassFin? {
        return listFin[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listFin.size
    }
}