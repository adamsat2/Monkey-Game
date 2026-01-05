package com.example.monkeygame

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.monkeygame.utilities.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ScoreEntry(
    val name: String,
    val score: Int,
    val lat: Double,
    val lon: Double
)

class ScoreActivity : AppCompatActivity() {
    private lateinit var score_LBL_score: MaterialTextView
    private lateinit var score_ET_name: TextInputEditText
    private lateinit var score_BTN_save: MaterialButton
    private var currentScore = 0

    // Placeholder coordinates
    private val defLat = 32.109333
    private val defLon = 34.855499
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
            checkLocationPermissionAndSave()
        }
    }

    private fun checkLocationPermissionAndSave() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Location denied. Saving with default.", Toast.LENGTH_SHORT).show()
                saveScore(defLat, defLon)
            }
        }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        saveScore(location.latitude, location.longitude)
                    } else {
                        Toast.makeText(this, "Location not found. Using default.", Toast.LENGTH_SHORT).show()
                        saveScore(defLat, defLon)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show()
                    saveScore(defLat, defLon)
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun saveScore(lat: Double, lon: Double) {
        val name = score_ET_name.text.toString().ifEmpty { "Anonymous" }

        val newEntry = ScoreEntry(name, currentScore, lat, lon)

        val gson = Gson()
        val sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val json = sp.getString(Constants.BundleKeys.SCORE_LIST_KEY, null)
        val type = object : TypeToken<MutableList<ScoreEntry>>() {}.type

        val scoresList: MutableList<ScoreEntry> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        scoresList.add(newEntry)
        scoresList.sortByDescending { it.score }

        // keep only top 10 records
        if (scoresList.size > 10) {
            scoresList.removeAt(scoresList.size - 1)
        }

        sp.edit().putString(Constants.BundleKeys.SCORE_LIST_KEY, gson.toJson(scoresList)).apply()

        // proceed only after saving
        val intent = Intent(this, LeaderboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}

