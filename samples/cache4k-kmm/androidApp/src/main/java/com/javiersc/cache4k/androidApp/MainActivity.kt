package com.javiersc.cache4k.androidApp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.javiersc.cache4k.shared.User
import com.javiersc.cache4k.shared.userCache

class MainActivity : AppCompatActivity() {

    init {
        userCache.put(1, User(1, "Javier"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)

        tv.text = userCache.get(1)?.name
    }
}
