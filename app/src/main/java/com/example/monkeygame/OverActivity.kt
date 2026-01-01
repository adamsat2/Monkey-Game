package com.example.monkeygame

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monkeygame.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class OverActivity : AppCompatActivity() {


    private lateinit var over_LBL_title: MaterialTextView
    private lateinit var over_BTN_newGame: MaterialButton
    private lateinit var over_LBL_score: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_over)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
    }

    private fun findViews() {
        over_LBL_title = findViewById(R.id.over_LBL_title)
        over_LBL_score = findViewById(R.id.over_LBL_score)
        over_BTN_newGame = findViewById(R.id.over_BTN_newGame)
    }

    private fun initViews() {
        val bundle: Bundle? = intent.extras
        val message = bundle?.getString(Constants.BundleKeys.MESSAGE_KEY,"🤷🏻‍♂️ Unknown Status!")
        val score = bundle?.getInt(Constants.BundleKeys.SCORE_KEY, 0)

        over_LBL_title.text = message
        over_LBL_score.text = "Score: $score"
        over_BTN_newGame.setOnClickListener { view: View ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}