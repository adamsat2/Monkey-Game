package com.example.monkeygame.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.monkeygame.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
        }

        return v
    }

    fun zoom(lat: Double, lon: Double) {
        if (::googleMap.isInitialized) {
            val location = LatLng(lat, lon)
            googleMap.clear() // Clear old markers
            googleMap.addMarker(MarkerOptions().position(location).title("Score Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}