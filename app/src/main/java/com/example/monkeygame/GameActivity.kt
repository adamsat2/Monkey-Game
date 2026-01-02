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
import com.example.monkeygame.utilities.TiltDetector
import com.example.monkeygame.interfaces.TiltCallback
import com.example.monkeygame.utilities.SingleSoundPlayer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class GameActivity : AppCompatActivity() {

    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var main_BTN_left: FloatingActionButton
    private lateinit var main_BTN_right: FloatingActionButton
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_IMG_background: AppCompatImageView
    private lateinit var main_IMG_grid: Array<Array<AppCompatImageView>>

    private lateinit var gameManager: GameManager

    private lateinit var tiltDetector: TiltDetector
    private var isTiltMode: Boolean = false

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0
    private var timerOn: Boolean = false
    private var delay = Constants.Timer.DELAY
    private val ssp = SingleSoundPlayer(this) // used to manage sounds

    val runnable: Runnable = object : Runnable {
        override fun run() {
            // Reschedule:
            handler.postDelayed(this, delay)
            gameManager.moveObstacles()
            refreshUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isTiltMode = intent.getBooleanExtra(Constants.BundleKeys.TILT_KEY, false)
        val isFast = intent.getBooleanExtra(Constants.BundleKeys.FAST_GAME_KEY, false)

        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()

        if (isTiltMode) {
            initTiltDetector()
        }
        if (isFast) {
            delay /= 2
        }
    }

    override fun onResume() {
        super.onResume()
        startTimer()
        if (isTiltMode) {
            tiltDetector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        if (isTiltMode) {
            tiltDetector.stop()
        }
    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            this,
            object : TiltCallback {
                override fun tiltLeft() {
                    movePlayer(-1)
                }

                override fun tiltRight() {
                    movePlayer(1)
                }
            }
        )
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable)
        timerOn = false
    }

    private fun startTimer() {
        if (!timerOn) {
            startTime = System.currentTimeMillis()
            handler.postDelayed(runnable, delay)
            timerOn = true
        }
    }

    private fun findViews() {
        main_IMG_background = findViewById(R.id.main_IMG_background)
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_grid = arrayOf(
            arrayOf(findViewById(R.id.main_IMG_grid00), findViewById(R.id.main_IMG_grid01), findViewById(R.id.main_IMG_grid02), findViewById(R.id.main_IMG_grid03), findViewById(R.id.main_IMG_grid04)),
            arrayOf(findViewById(R.id.main_IMG_grid10), findViewById(R.id.main_IMG_grid11), findViewById(R.id.main_IMG_grid12), findViewById(R.id.main_IMG_grid13), findViewById(R.id.main_IMG_grid14)),
            arrayOf(findViewById(R.id.main_IMG_grid20), findViewById(R.id.main_IMG_grid21), findViewById(R.id.main_IMG_grid22), findViewById(R.id.main_IMG_grid23), findViewById(R.id.main_IMG_grid24)),
            arrayOf(findViewById(R.id.main_IMG_grid30), findViewById(R.id.main_IMG_grid31), findViewById(R.id.main_IMG_grid32), findViewById(R.id.main_IMG_grid33), findViewById(R.id.main_IMG_grid34)),
            arrayOf(findViewById(R.id.main_IMG_grid40), findViewById(R.id.main_IMG_grid41), findViewById(R.id.main_IMG_grid42), findViewById(R.id.main_IMG_grid43), findViewById(R.id.main_IMG_grid44))
        )
    }

    private fun initViews() {
        // Hide buttons if Tilt Mode
        if (isTiltMode) {
            main_BTN_left.visibility = View.INVISIBLE
            main_BTN_right.visibility = View.INVISIBLE
        }

        main_BTN_left.setOnClickListener { view: View -> movePlayer(-1) }
        main_BTN_right.setOnClickListener { view: View -> movePlayer(1) }
        refreshUI()
    }

    private fun movePlayer(move: Int) {
        if (!gameManager.isGameOver) {
            gameManager.movePlayer(move)
            refreshUI()
        }
    }

    private fun refreshUI() {
        // check collision before anything else to be instantly notified when out of lives
        val collisionType = gameManager.checkCollision()

        if (collisionType == 1) {
            SignalManager.getInstance().toast("Ouch! Kofifi was hit by a barrel!", SignalManager.ToastLength.SHORT)
            SignalManager.getInstance().vibrate()
            ssp.playSound(R.raw.monkey_grunt)
        }
        else if (collisionType == 2) {
            SignalManager.getInstance().toast("Ohhhh banana", SignalManager.ToastLength.SHORT)
            ssp.playSound(R.raw.oh_banana)
        }

        main_LBL_score.text = "Score: ${gameManager.score}"

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
                // barrels and bananas:
                else {
                    val item = gameManager.getItem(i, j)
                    if (item == 1) {
                        img.setImageResource(R.drawable.barrel)
                        img.visibility = View.VISIBLE
                    } else if (item == 2) {
                        img.setImageResource(R.drawable.banana)
                        img.visibility = View.VISIBLE
                    }
                }
            }
        }

        // hearts:
        for (i in 0 until main_IMG_hearts.size) {
            if (i < main_IMG_hearts.size - gameManager.hitsTaken) {
                main_IMG_hearts[i].visibility = View.VISIBLE
            } else {
                main_IMG_hearts[i].visibility = View.INVISIBLE
            }
        }

        // lost:
        if (gameManager.isGameOver) {
            // calculate the total score including 10 points per banana eaten
            val totalScore = gameManager.score + (gameManager.bananasEaten * 10)
            Log.d("Game Status", "Game Over!")
            changeActivity("Game Over\nYou Lost 😭", totalScore)
            stopTimer()
        }
    }

    private fun changeActivity(message: String, score: Int) {
        val intent = Intent(this, OverActivity::class.java)
        val bundle = Bundle()
        bundle.putString(Constants.BundleKeys.MESSAGE_KEY, message)
        bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}