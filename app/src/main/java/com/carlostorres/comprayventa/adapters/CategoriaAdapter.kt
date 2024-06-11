package com.carlostorres.comprayventa.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.carlostorres.comprayventa.RvListenerCategoria
import com.carlostorres.comprayventa.databinding.ItemCategoriaInicioBinding
import com.carlostorres.comprayventa.model.CategoriaModel
import java.sql.Array
import java.util.Random

class CategoriaAdapter(
    private val context : Context,
    private val categoriaArrayList : ArrayList<CategoriaModel>,
    private val rvListenerCategoria: RvListenerCategoria
) : Adapter<CategoriaAdapter. HolderCategoria>() {

    private lateinit var binding : ItemCategoriaInicioBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaInicioBinding.inflate(LayoutInflater.from(context), parent, false)
        return  HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return  categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modeloCategoria = categoriaArrayList[position]

        val icono = modeloCategoria.ic
        val categoria = modeloCategoria.categoria

        val random = Random()
        val color = Color.argb(
            255,
            random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)
        )

        holder.categoriaIconoIV.setImageResource(icono)
        holder.categoriaTv.text = categoria
        holder.categoriaIconoIV.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            rvListenerCategoria.onCategoriaClick(modeloCategoria)
        }

    }

    inner class HolderCategoria(itemView : View) : ViewHolder(itemView){

        var categoriaIconoIV = binding.catIconoIv
        var categoriaTv = binding.tvCategoria

    }


}