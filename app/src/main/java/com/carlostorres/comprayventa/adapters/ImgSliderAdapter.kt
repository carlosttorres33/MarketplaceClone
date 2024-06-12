package com.carlostorres.comprayventa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.databinding.ItemImagenSliderBinding
import com.carlostorres.comprayventa.model.ImgSliderModel
import com.google.android.material.imageview.ShapeableImageView

class ImgSliderAdapter : RecyclerView.Adapter<ImgSliderAdapter.HolderImagenSlider> {

    private lateinit var binding : ItemImagenSliderBinding

    private var context : Context
    private var imagenArrayList : ArrayList<ImgSliderModel>

    constructor(context: Context, imagenArrayList: ArrayList<ImgSliderModel>) {
        this.context = context
        this.imagenArrayList = imagenArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSlider {

        binding = ItemImagenSliderBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImagenSlider(binding.root)

    }

    override fun getItemCount(): Int {

        return imagenArrayList.size

    }

    override fun onBindViewHolder(holder: HolderImagenSlider, position: Int) {

        val modeloImagenSlider = imagenArrayList[position]
        val imgUrl = modeloImagenSlider.imagenUrl
        val imgContador = "${position + 1} / ${imagenArrayList.size}"

        holder.imagenContador.text = imgContador

        try {
             Glide
                .with(context)
                .load(imgUrl)
                .placeholder(R.drawable.ic_image)
                .into(holder.imagenIv)
        }catch (e:Exception){

        }

        holder.imagenIv.setOnClickListener {

        }

    }

    inner class HolderImagenSlider(itemView : View) : RecyclerView.ViewHolder(itemView){

        var imagenIv : ShapeableImageView = binding.imagenIv
        var imagenContador : TextView = binding.imagenContadorTv

    }


}