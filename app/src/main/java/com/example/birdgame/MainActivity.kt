package com.example.birdgame

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var tvHighScore: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvHighScore = findViewById(R.id.score)
        val highScore = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getInt("highScore", 0)
        tvHighScore.text = highScore.toString()
    }
    fun startGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }
}