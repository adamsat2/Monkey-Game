package com.example.monkeygame

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monkeygame.logic.GameManager
import com.example.monkeygame.utilities.Constants
import com.example.monkeygame.utilities.SignalManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var main_BTN_left: FloatingActionButton

    private lateinit var main_BTN_right: FloatingActionButton

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    private lateinit var main_IMG_background: AppCompatImageView

    private lateinit var main_IMG_grid: Array<Array<AppCompatImageView>>

    private lateinit var gameManager: GameManager

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0
    private var timerOn: Boolean = false

    val runnable: Runnable = object : Runnable {
        override fun run() {
            // Reschedule:
            handler.postDelayed(this, Constants.Timer.DELAY)
            gameManager.moveObstacles()
            refreshUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
        timerOn = false
    }

    private fun startTimer() {
        if (!timerOn) {
            startTime = System.currentTimeMillis()
            handler.postDelayed(runnable, Constants.Timer.DELAY)
            timerOn = true
        }
    }

    private fun findViews() {
        main_IMG_background = findViewById(R.id.main_IMG_background)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_grid = arrayOf(
            arrayOf(findViewById(R.id.main_IMG_grid00), findViewById(R.id.main_IMG_grid01), findViewById(R.id.main_IMG_grid02)),
            arrayOf(findViewById(R.id.main_IMG_grid10), findViewById(R.id.main_IMG_grid11), findViewById(R.id.main_IMG_grid12)),
            arrayOf(findViewById(R.id.main_IMG_grid20), findViewById(R.id.main_IMG_grid21), findViewById(R.id.main_IMG_grid22)),
            arrayOf(findViewById(R.id.main_IMG_grid30), findViewById(R.id.main_IMG_grid31), findViewById(R.id.main_IMG_grid32)),
            arrayOf(findViewById(R.id.main_IMG_grid40), findViewById(R.id.main_IMG_grid41), findViewById(R.id.main_IMG_grid42))
        )
    }

    private fun initViews() {
        main_BTN_left.setOnClickListener { view: View -> movePlayer(-1) }
        main_BTN_right.setOnClickListener { view: View -> movePlayer(1) }
        refreshUI()
        startTimer()
    }

    private fun movePlayer(move: Int) {
        if (!gameManager.isGameOver) {
            gameManager.movePlayer(move)
            refreshUI()
        }
    }

    private fun refreshUI() {
        // check collision before anything else to be instantly notified when out of lives
        if (gameManager.checkCollision()) {
            SignalManager.getInstance().toast("Ouch! Kofifi was hit by a barrel!", SignalManager.ToastLength.SHORT)
            SignalManager.getInstance().vibrate()
        }

        val rows = gameManager.rows
        val cols = gameManager.cols

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val img: AppCompatImageView = main_IMG_grid[i][j]

                img.visibility = View.INVISIBLE

                // monkey:
                if (i == gameManager.monkeyRow && j == gameManager.monkeyCol) {
                    img.setImageResource(R.drawable.monkey)
                    img.visibility = View.VISIBLE
                }
                // barrels
                else if (gameManager.getItem(i, j) == 1) {
                    img.setImageResource(R.drawable.barrel)
                    img.visibility = View.VISIBLE
                }
            }
        }

        // hearts:
        if (gameManager.hitsTaken != 0){
            main_IMG_hearts[main_IMG_hearts.size - gameManager.hitsTaken]
                .visibility = View.INVISIBLE
        }
        // lost:
        if (gameManager.isGameOver) {
            Log.d("Game Status", "Game Over!")
            changeActivity("Game Over\nYou Lost 😭")
            stopTimer()
        }
    }

    private fun changeActivity(message: String) {
        val intent = Intent(this, OverActivity::class.java)
        var bundle = Bundle()
        bundle.putString(Constants.BundleKeys.MESSAGE_KEY, message)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}