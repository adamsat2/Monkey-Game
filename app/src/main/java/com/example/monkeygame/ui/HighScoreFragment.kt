package com.example.monkeygame.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monkeygame.R
import com.example.monkeygame.ScoreEntry
import com.example.monkeygame.interfaces.Callback_HighScoreClicked
import com.example.monkeygame.utilities.Constants
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HighScoreFragment : Fragment() {
    var highScoreItemClicked: Callback_HighScoreClicked? = null
    private lateinit var highScore_recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_high_score, container, false)
        highScore_recycler = v.findViewById(R.id.highScore_recycler)
        highScore_recycler.layoutManager = LinearLayoutManager(context)
        loadScores()
        return v
    }

    private fun loadScores() {
        val gson = Gson()
        val sp = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val json = sp.getString(Constants.BundleKeys.SCORE_LIST_KEY, null)
        val type = object : TypeToken<List<ScoreEntry>>() {}.type

        val scores: List<ScoreEntry> = if (json != null) gson.fromJson(json, type) else emptyList()

        highScore_recycler.adapter = ScoreAdapter(scores, highScoreItemClicked)
    }

    class ScoreAdapter(
        private val scores: List<ScoreEntry>,
        private val callback: Callback_HighScoreClicked?
    ) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

        class ScoreViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: MaterialTextView = v.findViewById(R.id.score_LBL_name)
            val score: MaterialTextView = v.findViewById(R.id.score_LBL_score)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_score, parent, false)
            return ScoreViewHolder(v)
        }

        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val item = scores[position]
            holder.name.text = item.name
            holder.score.text = item.score.toString()

            holder.itemView.setOnClickListener {
                callback?.highScoreItemClicked(item.lat, item.lon)
            }
        }

        override fun getItemCount() = scores.size
    }
}