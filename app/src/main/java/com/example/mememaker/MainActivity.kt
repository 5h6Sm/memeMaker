package com.example.mememaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent_btn = findViewById<Button>(R.id.clickMe) as Button

        intent_btn.setOnClickListener{
            val intent = Intent(this, SelectMemeActivity::class.java)
            startActivity(intent)
        }
    }
}