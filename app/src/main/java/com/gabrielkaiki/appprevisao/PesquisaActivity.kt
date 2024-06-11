package com.gabrielkaiki.appprevisao

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.gabrielkaiki.appprevisao.api.ClimaServices
import com.gabrielkaiki.appprevisao.api.clima_api_key
import com.gabrielkaiki.appprevisao.databinding.ActivityPesquisaBinding
import com.gabrielkaiki.appprevisao.model.Clima
import com.gabrielkaiki.appprevisao.retrofit.getRetrofit
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputEditText
import dmax.dialog.SpotsDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PesquisaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPesquisaBinding
    private lateinit var botaoPesquisa: Button
    private lateinit var campoTextoCidade: TextInputEditText
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesquisaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Anúncios
        carregarAnuncios()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Inicializar componentes
        inicializarComponentes()

        //OnClick
        definicaoClickListeners()
    }

    private fun carregarAnuncios() {
        adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun definicaoClickListeners() {
        botaoPesquisa.setOnClickListener {
            val cidade = campoTextoCidade?.text.toString()

            if (!cidade.isNullOrEmpty()) {
                fazerRequisicaoComRetrofit(cidade)
            } else {
                Toast.makeText(this@PesquisaActivity, "Please enter a city!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun fazerRequisicaoComRetrofit(cidade: String) {
        val dialog = SpotsDialog.Builder().setContext(this).build()
        dialog.show()

        val retrofit = getRetrofit()
        val requisicao: ClimaServices = retrofit.create(ClimaServices::class.java)

        requisicao.getClimaCidade(
            clima_api_key,
            "json-cors",
            cidade
        ).enqueue(object : Callback<Clima> {
            override fun onResponse(call: Call<Clima>, response: Response<Clima>) {

                if (response.body() != null) {
                    val clima: Clima = response.body()!!
                    val intent = Intent(this@PesquisaActivity, MainActivity::class.java)
                    intent.putExtra("clima", clima)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@PesquisaActivity,
                        "The city entered is invalid!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }

            override fun onFailure(call: Call<Clima>, t: Throwable) {
                Toast.makeText(this@PesquisaActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
        })
    }

    private fun inicializarComponentes() {
        botaoPesquisa = binding.buttonSearch
        campoTextoCidade = binding.textInputCidade
    }
}