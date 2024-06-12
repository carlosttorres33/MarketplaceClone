package com.carlostorres.comprayventa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.databinding.ItemAnuncioBinding
import com.carlostorres.comprayventa.model.AnuncioModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AnuncioAdapter : RecyclerView.Adapter<AnuncioAdapter.HolderAnuncio>{

    private lateinit var binding : ItemAnuncioBinding

    private var context : Context
    private var anuncioArrayList : ArrayList<AnuncioModel>
    private var firebaseAuth : FirebaseAuth

    constructor(context: Context, anuncioArrayList: ArrayList<AnuncioModel>) {
        this.context = context
        this.anuncioArrayList = anuncioArrayList
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAnuncio {
        binding = ItemAnuncioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAnuncio(binding.root)
    }

    override fun getItemCount(): Int {
        return anuncioArrayList.size
    }

    override fun onBindViewHolder(holder: HolderAnuncio, position: Int) {
        val modeloAnuncio = anuncioArrayList[position]

        val titulo = modeloAnuncio.titutlo
        val descripcion = modeloAnuncio.descripcion
        val direccion = modeloAnuncio.direccion
        val condicion = modeloAnuncio.condicion
        val precio = modeloAnuncio.precio
        val tiempo = modeloAnuncio.tiempo

        val formatoFecha = Constantes.obtenerFecha(tiempo)

        cargarPrimeraImgAnuncio(modeloAnuncio, holder)

        holder.tv_titulo.text = titulo
        holder.tv_descricion.text = descripcion
        holder.tv_direccion.text = direccion
        holder.tv_condicion.text = condicion
        holder.tv_precio.text = precio
        holder.tv_fecha.text = formatoFecha
    }

    private fun cargarPrimeraImgAnuncio(
        modeloAnuncio: AnuncioModel,
        holder: HolderAnuncio
    ) {

        val idAnuncio = modeloAnuncio.id

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio).child("Imagenes").limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        /***** Agregar el folder correspondiente ****/
                        val imagenUrl = "${ds.child("imagenUrl").value}"
                        try {
                            Glide.with(context)
                                .load(imagenUrl)
                                .placeholder(R.drawable.ic_image)
                                .into(holder.imagenIv)
                        }catch (e: Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    inner class  HolderAnuncio(itemView : View) : RecyclerView.ViewHolder(itemView){

        var imagenIv = binding.imagenIv
        var tv_titulo = binding.tvTitulo
        var tv_descricion = binding.tvDescripcion
        var tv_direccion = binding.tvDireccion
        var tv_condicion = binding.tvCondicion
        var tv_precio = binding.tvPrecio
        var tv_fecha = binding.tvFecha
        var ib_fav = binding.imbFav

    }


}