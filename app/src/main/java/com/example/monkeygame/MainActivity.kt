package com.example.monkeygame

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monkeygame.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var main_IMG_background: AppCompatImageView
    private lateinit var main_BTN_button: MaterialButton
    private lateinit var main_BTN_tilt: MaterialButton
    private lateinit var main_BTN_leaderboards: MaterialButton
    private lateinit var main_SW_fast: SwitchMaterial

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
        initViews()
    }

    private fun findViews() {
        main_IMG_background = findViewById(R.id.main_IMG_background)
        main_BTN_button = findViewById(R.id.main_BTN_button)
        main_BTN_tilt = findViewById(R.id.main_BTN_tilt)
        main_BTN_leaderboards = findViewById(R.id.main_BTN_leaderboards)
        main_SW_fast = findViewById(R.id.main_SW_fast)
    }

    private fun initViews() {
        main_BTN_button.setOnClickListener {
            startGame(false)
        }

        main_BTN_tilt.setOnClickListener {
            startGame(true)
        }

        main_BTN_leaderboards.setOnClickListener {
            // TODO: Open Leaderboards Activity
        }
    }

    private fun startGame(isTiltMode: Boolean) {
        val intent = Intent(this, GameActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(Constants.BundleKeys.TILT_KEY, isTiltMode)
        bundle.putBoolean(Constants.BundleKeys.FAST_GAME_KEY, main_SW_fast.isChecked)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}