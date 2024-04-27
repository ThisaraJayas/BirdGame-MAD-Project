package com.example.birdgame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import java.util.Random

class GameView(var gameContext: Context) : View(gameContext) {
    private var deviceWidth: Int
    private var deviceHeight: Int
    private var nest: Bitmap
    private var bird: Bitmap
    private var worm: Bitmap
    private var handler: Handler
    private var runnable: Runnable
    private val UPDATE_MILLIS: Long = 30
    private var birdX: Int
    private var birdY: Int
    private var wormX: Int
    private var wormY: Int
    private var random: Random
    private var wormAnimation = false
    private var points = 0
    private val TEXT_SIZE = 120f
    private var textPaint: Paint
    private var healthPaint: Paint
    private var life = 3
    private var handSpeed: Int

    // Declaring trashX and trashY as member variables
    private var trashX: Int
    private var trashY: Int

    private val sharedPreferences: SharedPreferences = gameContext.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
    fun saveHighScore(score: Int){
        val currentHighScore = getHighScore()
        if (score > currentHighScore) {
            with(sharedPreferences.edit()) {
                putInt("highScore", score)
                apply()
            }
        }

    }
    fun getHighScore(): Int {
        return sharedPreferences.getInt("highScore", 0)
    }
    init {
        val displayMetrics = resources.displayMetrics
        deviceWidth = displayMetrics.widthPixels
        deviceHeight = displayMetrics.heightPixels
        nest = BitmapFactory.decodeResource(resources, R.drawable.nest)
        bird = BitmapFactory.decodeResource(resources, R.drawable.bird)
        worm = BitmapFactory.decodeResource(resources, R.drawable.worm)
        handler = Handler()
        runnable = Runnable { invalidate() }
        random = Random()
        val minY = deviceHeight / 3
        val maxY = deviceHeight * 2 / 3

        birdX = deviceWidth + random.nextInt(300)
//        birdY = random.nextInt(600)
        birdY = minY + random.nextInt(maxY - minY)
        wormX = birdX
        wormY = birdY + bird.height - 30
        textPaint = Paint()
        textPaint.color = Color.rgb(255, 0, 0)
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        healthPaint = Paint()
        healthPaint.color = Color.GREEN
        handSpeed = 10 + random.nextInt(11)
        trashX = deviceWidth / 2 - nest.width / 2
        trashY = deviceHeight - nest.height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var backgroundImg = BitmapFactory.decodeResource(resources, R.drawable.gamebackground1)
        canvas.drawBitmap(backgroundImg, 0f,0f,null)
        if (!wormAnimation) {
            birdX -= handSpeed
            wormX -= handSpeed
        }
        if (birdX <= -bird.width) {
            birdX = deviceWidth + random.nextInt(300)
            wormX = birdX
            birdY = random.nextInt(600)
            wormY = birdY + bird.height - 30
            handSpeed = 10 + random.nextInt(11)
            life--
            if (life == 0) {
                saveHighScore(points)
                val intent = Intent(gameContext, GameOver::class.java)
                intent.putExtra("points", points)
                gameContext.startActivity(intent)
                (gameContext as Activity).finish()
            }
        }
        if (wormAnimation) {
            wormY += 40
        }
        if (wormAnimation && wormX + worm.width >= trashX && wormX <= trashX + nest.width && wormY + worm.height >= deviceHeight - nest.height && wormY <= deviceHeight) {
            birdX = deviceWidth + random.nextInt(300)
            wormX = birdX
            birdY = random.nextInt(600)
            wormY = birdY + bird.height - 30
            handSpeed = 10 + random.nextInt(11)
            points++
            trashX = bird.width + random.nextInt(deviceWidth - 2 * bird.width)
            wormAnimation = false
        }
        if (wormAnimation && wormY + worm.height >= deviceHeight) {
            life--
            if (life == 0) {
                saveHighScore(points)
                val intent = Intent(gameContext, GameOver::class.java)
                intent.putExtra("points", points)
                gameContext.startActivity(intent)
                (gameContext as Activity).finish()
            }
            birdX = deviceWidth + random.nextInt(300)
            wormX = birdX
            birdY = random.nextInt(600)
            wormY = birdY + bird.height - 30
            trashX = bird.width + random.nextInt(deviceWidth - 2 * bird.width)
            wormAnimation = false
        }
        canvas.drawBitmap(nest, trashX.toFloat(), trashY.toFloat(), null)
        canvas.drawBitmap(bird, birdX.toFloat(), birdY.toFloat(), null)
        canvas.drawBitmap(worm, wormX.toFloat(), wormY.toFloat(), null)
        canvas.drawText("$points", 20f, TEXT_SIZE, textPaint)
        if (life == 2) healthPaint.color = Color.YELLOW else if (life == 1) healthPaint.color =
            Color.RED
        canvas.drawRect(
            (deviceWidth - 200).toFloat(),
            30f,
            (deviceWidth - 200 + 60 * life).toFloat(),
            80f,
            healthPaint
        )
        if (life != 0) handler.postDelayed(runnable, UPDATE_MILLIS)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (!wormAnimation && touchX >= birdX && touchX <= birdY + bird.height) {
                wormAnimation = true
            }
        }
        return true
    }
}
