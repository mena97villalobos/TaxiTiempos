package com.mena97villalobos.taxitiempos.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mena97villalobos.taxitiempos.R
import net.danlew.android.joda.JodaTimeAndroid

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JodaTimeAndroid.init(this)
    }
}