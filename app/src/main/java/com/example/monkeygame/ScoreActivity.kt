package com.example.monkeygame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monkeygame.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreActivity : AppCompatActivity() {
    private lateinit var score_LBL_score: MaterialTextView
    private lateinit var score_ET_name: TextInputEditText
    private lateinit var score_BTN_save: MaterialButton
    private var currentScore = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        currentScore = intent.getIntExtra(Constants.BundleKeys.SCORE_KEY, 0)
        findViews()
        initViews()
    }

    private fun findViews() {
        score_LBL_score = findViewById(R.id.score_LBL_score)
        score_ET_name = findViewById(R.id.score_ET_name)
        score_BTN_save = findViewById(R.id.score_BTN_save)
    }

    private fun initViews() {
        score_LBL_score.text = "Score: $currentScore"
        score_BTN_save.setOnClickListener {
            saveScore()
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveScore() {
        val name = score_ET_name.text.toString().ifEmpty { "Anonymous" }
        // Placeholder coordinates for now
        val lat = 32.109333
        val lon = 34.855499

        val newEntry = ScoreEntry(name, currentScore, lat, lon)

        val gson = Gson()
        val sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val json = sp.getString(Constants.BundleKeys.SAVE_SCORE_KEY, null)
        val type = object : TypeToken<MutableList<ScoreEntry>>() {}.type

        val scoresList: MutableList<ScoreEntry> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        scoresList.add(newEntry)
        scoresList.sortByDescending { it.score }

        sp.edit().putString(Constants.BundleKeys.SAVE_SCORE_KEY, gson.toJson(scoresList)).apply()
    }
}

data class ScoreEntry(
    val name: String,
    val score: Int,
    val lat: Double,
    val lon: Double
)