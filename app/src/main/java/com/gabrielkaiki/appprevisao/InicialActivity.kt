package com.gabrielkaiki.appprevisao

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.gabrielkaiki.appprevisao.api.ClimaServices
import com.gabrielkaiki.appprevisao.api.clima_api_key
import com.gabrielkaiki.appprevisao.databinding.ActivityInicialBinding
import com.gabrielkaiki.appprevisao.model.Clima
import com.gabrielkaiki.appprevisao.permissoes.Permissoes
import com.gabrielkaiki.appprevisao.retrofit.getRetrofit
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InicialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInicialBinding
    lateinit var locationCallback: LocationCallback
    private lateinit var mAdView2: AdView
    private lateinit var search_button: Button
    private lateinit var gps_button: Button
    private var permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        //Iniciar SDK e anúncios
        inicializarSDKAnuncios()

        supportActionBar!!.hide()

        search_button = binding.searchButton
        gps_button = binding.gpsButton

        search_button.setOnClickListener {
            search_button.setBackgroundResource(R.drawable.button_form_selected)
            search_button.setTextColor(Color.WHITE)
            search_button.compoundDrawables[0].setTint(Color.WHITE)
            startActivity(Intent(this@InicialActivity, PesquisaActivity::class.java))
        }

        gps_button.setOnClickListener {
            //Permissões
            if (!Permissoes.validarPermissoes(permission, this, 1)) {
                pesquisarViaGps()
            }
            gps_button.setBackgroundResource(R.drawable.button_form_selected)
            gps_button.setTextColor(Color.WHITE)
            gps_button.compoundDrawables[0].setTint(Color.WHITE)
        }
    }

    private fun inicializarSDKAnuncios() {
        mAdView2 = findViewById(R.id.adView2)
        val adRequest2 = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var permissoesNegadas = 0
        for (permissaoResultado in grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaPermissao()
                permissoesNegadas++
            }
        }

        if (permissoesNegadas == 0) pesquisarViaGps()
    }

    private fun alertaPermissao() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permissões negadas")
            .setMessage("Para usar este recurso é necessário aceitar todas as permissões.")
            .setCancelable(false)

        builder.setPositiveButton("Ok") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun pesquisarViaGps() {
        val dialog: AlertDialog = SpotsDialog.Builder().setContext(this).build()
        dialog.show()

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 2 * 3000
        locationRequest.priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(settingsRequest).addOnSuccessListener {
        }.addOnFailureListener {
            //dialog.dismiss()
            if (it is ResolvableApiException) {
                val resolvableApiException: ResolvableApiException = it
                resolvableApiException.startResolutionForResult(this, 1)
            }
        }

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(var1: LocationResult) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                val localizacao =
                    LatLng(var1.lastLocation!!.latitude, var1.lastLocation!!.longitude)
                obterLocalizacaoViaLatLon(localizacao)
                dialog.dismiss()
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun obterLocalizacaoViaLatLon(localizacao: LatLng) {
        val dialog: AlertDialog = SpotsDialog.Builder().setContext(this).build()
        dialog.show()

        val retrofit = getRetrofit()
        val requisicao: ClimaServices = retrofit.create(ClimaServices::class.java)

        requisicao.getClima(
            clima_api_key,
            "json-cors",
            localizacao.latitude.toString(),
            localizacao.longitude.toString(),
            "remote"
        ).enqueue(object : Callback<Clima> {
            override fun onResponse(call: Call<Clima>, response: Response<Clima>) {

                if (response.body() != null) {
                    val clima: Clima = response.body()!!
                    val intent = Intent(this@InicialActivity, MainActivity::class.java)
                    intent.putExtra("clima", clima)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@InicialActivity,
                        "City not available!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<Clima>, t: Throwable) {
                Toast.makeText(this@InicialActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        gps_button.setBackgroundResource(R.drawable.button_form)
        search_button.setBackgroundResource(R.drawable.button_form)
        search_button.compoundDrawables[0].setTint(getResources().getColor(R.color.light_blue))
        gps_button.compoundDrawables[0].setTint(getResources().getColor(R.color.light_blue))
    }
}