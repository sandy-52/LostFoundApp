package com.example.lostfoundapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var db: DatabaseHelper

    private var userLat = 0.0
    private var userLng = 0.0

    // Radius-based search: only show posts within this distance
    private val radiusKm = 10000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        db = DatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        getCurrentLocationAndShowItems()
    }

    private fun getCurrentLocationAndShowItems() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                300
            )
            return
        }

        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            if (location != null) {
                userLat = location.latitude
                userLng = location.longitude

                val userPosition = LatLng(userLat, userLng)

                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(userPosition, 12f)
                )

                googleMap.addMarker(
                    MarkerOptions()
                        .position(userPosition)
                        .title("My Current Location")
                )

                showPostsWithinRadius()
            } else {
                Toast.makeText(
                    this,
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPostsWithinRadius() {
        val posts = db.getAllPosts()

        for (post in posts) {

            val results = FloatArray(1)

            Location.distanceBetween(
                userLat,
                userLng,
                post.latitude,
                post.longitude,
                results
            )

            val distanceKm = results[0] / 1000

            if (distanceKm <= radiusKm) {

                val position = LatLng(post.latitude, post.longitude)

                googleMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(post.name)
                        .snippet("${post.type} - ${post.description}")
                )
            }
        }
    }
}