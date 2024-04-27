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
    private var trash: Bitmap
    private var hand: Bitmap
    private var plastic: Bitmap
    private var handler: Handler
    private var runnable: Runnable
    private val UPDATE_MILLIS: Long = 30
    private var handX: Int
    private var handY: Int
    private var plasticX: Int
    private var plasticY: Int
    private var random: Random
    private var plasticAnimation = false
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
        trash = BitmapFactory.decodeResource(resources, R.drawable.nest)
        hand = BitmapFactory.decodeResource(resources, R.drawable.bird)
        plastic = BitmapFactory.decodeResource(resources, R.drawable.worm)
        handler = Handler()
        runnable = Runnable { invalidate() }
        random = Random()
        val minY = deviceHeight / 3  // Adjust this value as needed to set the minimum Y position
        val maxY = deviceHeight * 2 / 3  // Adjust this value as needed to set the maximum Y position

        handX = deviceWidth + random.nextInt(300)
//        handY = random.nextInt(600)
        handY = minY + random.nextInt(maxY - minY)
        plasticX = handX
        plasticY = handY + hand.height - 30
        textPaint = Paint()
        textPaint.color = Color.rgb(255, 0, 0)
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.LEFT
        healthPaint = Paint()
        healthPaint.color = Color.GREEN
        handSpeed = 10 + random.nextInt(11)
        trashX = deviceWidth / 2 - trash.width / 2
        trashY = deviceHeight - trash.height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawColor(Color.BLUE)
        var backgroundImg = BitmapFactory.decodeResource(resources, R.drawable.gamebackground1)
        canvas.drawBitmap(backgroundImg, 0f,0f,null)
        if (!plasticAnimation) {
            handX -= handSpeed
            plasticX -= handSpeed
        }
        if (handX <= -hand.width) {
            handX = deviceWidth + random.nextInt(300)
            plasticX = handX
            handY = random.nextInt(600)
            plasticY = handY + hand.height - 30
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
        if (plasticAnimation) {
            plasticY += 40
        }
        if (plasticAnimation && plasticX + plastic.width >= trashX && plasticX <= trashX + trash.width && plasticY + plastic.height >= deviceHeight - trash.height && plasticY <= deviceHeight) {
            handX = deviceWidth + random.nextInt(300)
            plasticX = handX
            handY = random.nextInt(600)
            plasticY = handY + hand.height - 30
            handSpeed = 10 + random.nextInt(11)
            points++
            trashX = hand.width + random.nextInt(deviceWidth - 2 * hand.width)
            plasticAnimation = false
        }
        if (plasticAnimation && plasticY + plastic.height >= deviceHeight) {
            life--
            if (life == 0) {
                saveHighScore(points)
                val intent = Intent(gameContext, GameOver::class.java)
                intent.putExtra("points", points)
                gameContext.startActivity(intent)
                (gameContext as Activity).finish()
            }
            handX = deviceWidth + random.nextInt(300)
            plasticX = handX
            handY = random.nextInt(600)
            plasticY = handY + hand.height - 30
            trashX = hand.width + random.nextInt(deviceWidth - 2 * hand.width)
            plasticAnimation = false
        }
        canvas.drawBitmap(trash, trashX.toFloat(), trashY.toFloat(), null)
        canvas.drawBitmap(hand, handX.toFloat(), handY.toFloat(), null)
        canvas.drawBitmap(plastic, plasticX.toFloat(), plasticY.toFloat(), null)
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
            if (!plasticAnimation && touchX >= handX && touchX <= handY + hand.height) {
                plasticAnimation = true
            }
        }
        return true
    }
}
