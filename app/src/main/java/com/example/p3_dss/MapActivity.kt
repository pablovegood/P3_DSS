package com.example.p3_dss

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.*
import com.google.android.material.appbar.MaterialToolbar

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    data class Store(val name: String, val position: LatLng)

    private var userLatLng: LatLng? = null
    private var allStores: List<Store> = emptyList()

    private val requestFineLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                enableUserLocationAndLoadStores()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado. Mostrando mapa sin tu posición.", Toast.LENGTH_SHORT).show()
                showDefaultAreaAndStores()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        supportActionBar?.title = "Almacenes Cerca"

        // Bottom Navigation (igual que tenías)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_map
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_products -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.nav_cart -> { startActivity(Intent(this, CartActivity::class.java)); true }
                R.id.nav_map -> true
                else -> false
            }
        }

        // Mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMarkerClickListener { marker ->
            val store = marker.tag as? Store
            if (store != null) {
                openDirectionsTo(store)
                true
            } else {
                false
            }
        }

        checkLocationPermissionAndLoad()
    }

    private fun checkLocationPermissionAndLoad() {
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            enableUserLocationAndLoadStores()
        } else {
            requestFineLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableUserLocationAndLoadStores() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        map.isMyLocationEnabled = true

        val fused = LocationServices.getFusedLocationProviderClient(this)
        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    userLatLng = LatLng(loc.latitude, loc.longitude)
                    loadMockStoresNearUser()
                } else {
                    // Si no hay lastLocation, cae a zona por defecto
                    showDefaultAreaAndStores()
                }
            }
            .addOnFailureListener {
                showDefaultAreaAndStores()
            }
    }

    private fun showDefaultAreaAndStores() {
        // Zona por defecto (Granada centro aprox). Cambia si quieres.
        val fallback = LatLng(37.176487, -3.597929)
        userLatLng = fallback
        loadMockStoresNearUser()
    }

    private fun loadMockStoresNearUser() {
        val user = userLatLng ?: return

        // Genera almacenes mock cerca (ej: dentro de 6 km)
        allStores = generateMockStores(center = user, count = 12, maxDistanceMeters = 6000)

        // Filtra "cerca" (ej: 3000 m)
        val nearby = allStores.filter { distanceMeters(user, it.position) <= 3000f }

        // Pintar mapa
        map.clear()

        // Centrar cámara en usuario
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 13f))

        // Marcadores de almacenes cercanos
        if (nearby.isEmpty()) {
            Toast.makeText(this, "No hay almacenes cerca. Sube el radio o cambia el filtro.", Toast.LENGTH_SHORT).show()
        } else {
            nearby.forEach { store ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(store.position)
                        .title(store.name)
                        .snippet("Toca para obtener indicaciones")
                )
                marker?.tag = store
            }
        }
    }

    private fun openDirectionsTo(store: Store) {
        // Abre Google Maps con indicaciones al almacén
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${store.position.latitude},${store.position.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        // Si no hay Google Maps instalado, abre con navegador
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun distanceMeters(a: LatLng, b: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results)
        return results[0]
    }

    private fun generateMockStores(center: LatLng, count: Int, maxDistanceMeters: Int): List<Store> {
        // Distribución pseudo-aleatoria alrededor del usuario (sin depender de APIs)
        val list = mutableListOf<Store>()
        for (i in 1..count) {
            val dist = (500..maxDistanceMeters).random().toDouble()
            val bearing = (0..359).random().toDouble()
            val pos = moveLatLng(center, dist, bearing)
            list += Store(name = "Almacén #$i", position = pos)
        }
        return list
    }

    // Mueve un punto por distancia (m) y rumbo (grados), aproximación esférica
    private fun moveLatLng(from: LatLng, distanceMeters: Double, bearingDegrees: Double): LatLng {
        val R = 6371000.0 // radio Tierra
        val bearing = Math.toRadians(bearingDegrees)
        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)

        val lat2 = asin(
            sin(lat1) * cos(distanceMeters / R) +
                    cos(lat1) * sin(distanceMeters / R) * cos(bearing)
        )

        val lon2 = lon1 + atan2(
            sin(bearing) * sin(distanceMeters / R) * cos(lat1),
            cos(distanceMeters / R) - sin(lat1) * sin(lat2)
        )

        return LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }
}
