package com.carlostorres.comprayventa.filtro

import android.widget.Filter
import com.carlostorres.comprayventa.adapters.AnuncioAdapter
import com.carlostorres.comprayventa.model.AnuncioModel
import java.util.Locale

class FiltrarAnuncio(
    private val adapter : AnuncioAdapter,
    private val filtroLista : ArrayList<AnuncioModel>
) : Filter() {

    override fun performFiltering(filtro: CharSequence?): FilterResults {

        var fillter = filtro
        val resultados = FilterResults()

        if (!fillter.isNullOrEmpty()){

            fillter = fillter.toString().uppercase(Locale.getDefault())
            val filtroModelo = ArrayList<AnuncioModel>()

            for (i in filtroLista.indices){
                if (
                    filtroLista[i].marca.uppercase(Locale.getDefault()).contains(fillter) ||
                    filtroLista[i].categoria.uppercase(Locale.getDefault()).contains(fillter) ||
                    filtroLista[i].titulo.uppercase(Locale.getDefault()).contains(fillter) ||
                    filtroLista[i].condicion.uppercase(Locale.getDefault()).contains(fillter)
                ){

                    filtroModelo.add(filtroLista[i])

                }
            }

            resultados.count = filtroModelo.size
            resultados.values = filtroModelo

        } else {

            resultados.count = filtroLista.size
            resultados.values = filtroLista

        }

        return resultados

    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults) {

        adapter.anuncioArrayList = resultados.values as ArrayList<AnuncioModel>
        adapter.notifyDataSetChanged()

    }
}