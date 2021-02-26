package com.example.kotlin1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin1.adapter.LineAdapter
import com.example.kotlin1.dataclasses.Place
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var myList: ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        myList = ArrayList<Place>()

        for (i in 0 until 500) {
            myList.add(Place("Country $i", i*500, "Capital $i"))
        }

        recycler_view.adapter = LineAdapter(myList)
        recycler_view.layoutManager = LinearLayoutManager(this)
        //recycler_view.setHasFixedSize(true)
    }


    fun insert(view: View) {
        myList.add(0, Place("Country XXX", 999, "Capital XXX"))
        recycler_view.adapter?.notifyDataSetChanged()

    }
}