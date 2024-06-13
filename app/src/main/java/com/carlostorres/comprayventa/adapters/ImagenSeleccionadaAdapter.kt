package com.carlostorres.comprayventa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.databinding.ItemImagenesSeleccionadasBinding
import com.carlostorres.comprayventa.model.ImagenSeleccionadaModel

class ImagenSeleccionadaAdapter(
    private val context : Context,
    private val imagenesSeleccionadas : ArrayList<ImagenSeleccionadaModel>
) : Adapter<ImagenSeleccionadaAdapter.HolderImagenSeleccionada>() {

    private lateinit var binding : ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenesSeleccionadas.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {

        val modelo = imagenesSeleccionadas[position]

        if (modelo.deInternet){
            try {

                val imagenUrl = modelo.imagenUrl

                Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.item_imagen)
                    .into(binding.itemImagen)

            }catch (e:Exception){

            }
        }else{
            try {

                val imagenUri = modelo.imagenUri

                Glide.with(context)
                    .load(imagenUri)
                    .placeholder(R.drawable.item_imagen)
                    .into(holder.item_imagen)

            } catch (e:Exception){

            }
        }

        holder.btn_cerrar.setOnClickListener {
            imagenesSeleccionadas.remove(modelo)
            notifyDataSetChanged()
        }
    }

    inner class HolderImagenSeleccionada(itemView : View) : ViewHolder(itemView){

        var item_imagen = binding.itemImagen
        var btn_cerrar = binding.cerrarItem

    }

}