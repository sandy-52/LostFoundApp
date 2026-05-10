package com.example.lostfoundapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.lostfoundapp.databinding.ActivityCreatePostBinding
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var db: DatabaseHelper
    private var selectedImageUri: Uri? = null

    private val imagePickerCode = 100
    private val locationPickerCode = 101

    private var selectedLat = 0.0
    private var selectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyAMiqlRjOrO8WAKniWdVidPxtpQPMlsCgM")
        }

        val categories = arrayOf("Electronics", "Pets", "Wallets", "Keys", "Clothing", "Other")
        binding.spinnerCategory.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        binding.btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            startActivityForResult(intent, imagePickerCode)
        }

        binding.etLocation.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
            ).build(this)

            startActivityForResult(intent, locationPickerCode)
        }

        binding.btnCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        binding.btnSave.setOnClickListener {
            savePost()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imagePickerCode && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data

            selectedImageUri?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                binding.imagePreview.setImageURI(uri)
            }
        }

        if (requestCode == locationPickerCode && resultCode == RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data!!)

            binding.etLocation.setText(place.address)

            place.latLng?.let {
                selectedLat = it.latitude
                selectedLng = it.longitude
            }
        }
    }

    private fun getCurrentLocation() {
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
                200
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                selectedLat = location.latitude
                selectedLng = location.longitude

                binding.etLocation.setText("$selectedLat, $selectedLng")
            } else {
                Toast.makeText(
                    this,
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun savePost() {
        val type = if (binding.radioLost.isChecked) "Lost" else "Found"
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val description = binding.etDescription.text.toString()
        val date = binding.etDate.text.toString()
        val location = binding.etLocation.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() ||
            date.isEmpty() || location.isEmpty() || selectedImageUri == null
        ) {
            Toast.makeText(
                this,
                "Please complete all fields and upload an image",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedLat == 0.0 && selectedLng == 0.0) {
            Toast.makeText(
                this,
                "Please select a valid location",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val timestamp = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        Toast.makeText(
            this,
            "Saving location: $selectedLat, $selectedLng",
            Toast.LENGTH_LONG
        ).show()

        val post = Post(
            type = type,
            name = name,
            phone = phone,
            description = description,
            date = date,
            location = location,
            latitude = selectedLat,
            longitude = selectedLng,
            category = category,
            imageUri = selectedImageUri.toString(),
            timestamp = timestamp
        )

        if (db.insertPost(post)) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show()
        }
    }
}