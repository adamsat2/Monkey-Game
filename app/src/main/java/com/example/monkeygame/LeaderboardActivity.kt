package com.example.monkeygame

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monkeygame.interfaces.Callback_HighScoreClicked
import com.example.monkeygame.ui.HighScoreFragment
import com.example.monkeygame.ui.MapFragment

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var mapFragment: MapFragment
    private lateinit var highScoreFragment: HighScoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mapFragment = MapFragment()
        highScoreFragment = HighScoreFragment()

        highScoreFragment.highScoreItemClicked = object : Callback_HighScoreClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.zoom(lat, lon)
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.main_FRAME_list, highScoreFragment)
            .add(R.id.main_FRAME_map, mapFragment)
            .commit()
    }
}
