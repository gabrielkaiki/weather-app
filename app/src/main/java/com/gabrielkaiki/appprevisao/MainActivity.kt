package com.gabrielkaiki.appprevisao

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.appprevisao.adapter.AdapterPrevisao
import com.gabrielkaiki.appprevisao.api.codigoCondicao
import com.gabrielkaiki.appprevisao.api.codigoTraducao
import com.gabrielkaiki.appprevisao.databinding.ActivityMainBinding
import com.gabrielkaiki.appprevisao.model.Clima
import com.gabrielkaiki.appprevisao.model.DiaClima
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerViewPrevisao: RecyclerView
    private lateinit var adapterPrevisao: AdapterPrevisao
    private lateinit var previsoes: ArrayList<DiaClima>
    private lateinit var campoTemperatura: TextView
    private lateinit var campoData: TextView
    private lateinit var campoHora: TextView
    private lateinit var campoHumidade: TextView
    private lateinit var campoVento: TextView
    private lateinit var campoClima: TextView
    private lateinit var campoCidade: TextView
    private lateinit var imagemClimaCidade: ImageView
    private lateinit var anuncio: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0F
        supportActionBar!!.title = ""

        //Inicializar componentes
        inicializarComponentes()

        //Recuperar informações da cidade pesquisada
        recuperarInformacoesDaCidade()

        //Configuração ReciclerView
        configurarReciclerView()
    }

    private fun recuperarInformacoesDaCidade() {
        val bundle = intent.extras
        if (bundle != null) {
            var clima: Clima = bundle.getSerializable("clima") as Clima

            previsoes = clima.results!!.forecast
            //Atribuir informações do clima para componentes de interface

            campoClima.text =
                codigoTraducao.get(clima.results!!.condition_code!!.toInt()) as CharSequence?
            imagemClimaCidade.setImageResource(codigoCondicao.get(clima.results!!.condition_code!!.toInt()) as Int)

            campoCidade.text = "${clima.results?.city_name}"
            campoTemperatura.text = "${clima.results?.temp} ºC"
            campoVento.text = clima.results?.wind_speedy
            campoData.text = clima.results?.date
            campoHora.text = clima.results?.time
            campoHumidade.text = clima.results?.humidity + "%"
        }
    }

    private fun inicializarComponentes() {
        recyclerViewPrevisao = binding.recyclerPrevisao10Dias
        campoClima = binding.textClima
        campoVento = binding.textVento
        campoCidade = binding.textCidade
        campoData = binding.textData
        campoHora = binding.textHora
        campoHumidade = binding.textHumidade
        campoTemperatura = binding.textTemp
        imagemClimaCidade = binding.imageClima
    }

    private fun configurarReciclerView() {
        adapterPrevisao = AdapterPrevisao(previsoes, this)
        recyclerViewPrevisao.setHasFixedSize(true)
        recyclerViewPrevisao.layoutManager = LinearLayoutManager(this)
        recyclerViewPrevisao.adapter = adapterPrevisao
    }
}