package com.gabrielkaiki.appprevisao

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.gms.ads.MobileAds

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()

        Handler().postDelayed({
            abrirTelaPrincipal()
            finish()
        }, 1000)

    }

    fun abrirTelaPrincipal() {
        var intentMaps = Intent(this, InicialActivity::class.java)
        startActivity(intentMaps)
    }
}