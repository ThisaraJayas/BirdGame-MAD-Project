package com.example.birdgame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var tvPoints: TextView
    private lateinit var tvHighScore: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)
        tvPoints = findViewById(R.id.tvPoints)
        tvHighScore = findViewById(R.id.score)
        val points = intent.extras!!.getInt("points")
        tvPoints.setText("" + points)

        val highScore = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getInt("highScore", 0)
        tvHighScore.text = highScore.toString()
    }

    fun restart(view: View?) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View?) {
        finish()
    }
}
