package com.gabrielkaiki.appprevisao.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gabrielkaiki.appprevisao.R
import com.gabrielkaiki.appprevisao.api.descricaoCondicao
import com.gabrielkaiki.appprevisao.model.DiaClima
import java.util.*
import kotlin.collections.ArrayList

class AdapterPrevisao(var previsoes: ArrayList<DiaClima>, var contexto: Context) :
    RecyclerView.Adapter<AdapterPrevisao.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: TextView = itemView.findViewById(R.id.textDateAdapter)
        var diaSemana: TextView = itemView.findViewById(R.id.textWeekdayAdapter)
        var max: TextView = itemView.findViewById(R.id.textMaxAdapter)
        var min: TextView = itemView.findViewById(R.id.textMinAdapter)
        var descricao: TextView = itemView.findViewById(R.id.textDescricaoAdapter)
        var imagemClima: ImageView = itemView.findViewById(R.id.imageClimaAdapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_previsao_dias, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val diaPrevisao = previsoes.get(position)
        holder.data.text = diaPrevisao.date
        holder.descricao.text = formatarDescricao(diaPrevisao.condition)
        holder.diaSemana.text = traducaoDoDiaDaSemana(diaPrevisao.weekday)
        holder.imagemClima.setImageResource(descricaoCondicao.get(diaPrevisao.description) as Int)
        holder.max.text = "${diaPrevisao.max.toString()} ºC"
        holder.min.text = "${diaPrevisao.min.toString()} ºC"
    }

    private fun formatarDescricao(condition: String?): CharSequence? {
        var condicaoClimatica = condition!!.replace("_", " ", false)
        condicaoClimatica = condicaoClimatica!!.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        return condicaoClimatica
    }

    private fun traducaoDoDiaDaSemana(weekday: String?): CharSequence? {
        return when (weekday) {
            "Seg" -> {
                "Monday"
            }

            "Ter" -> {
                "Tuesday"
            }

            "Qua" -> {
                "Wednesday"
            }

            "Qui" -> {
                "Thursday"
            }

            "Sex" -> {
                "Friday"
            }

            "Sáb" -> {
                "Saturday"
            }

            "Dom" -> {
                "Sunday"
            }
            else -> {
                ""
            }
        }
    }

    override fun getItemCount(): Int {
        return previsoes.size
    }


}