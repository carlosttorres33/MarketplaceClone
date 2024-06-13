package com.carlostorres.comprayventa.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.databinding.ItemImagenesSeleccionadasBinding
import com.carlostorres.comprayventa.model.ImagenSeleccionadaModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ImagenSeleccionadaAdapter(
    private val context: Context,
    private val imagenesSeleccionadas: ArrayList<ImagenSeleccionadaModel>,
    private val idAnuncio: String
) : Adapter<ImagenSeleccionadaAdapter.HolderImagenSeleccionada>() {

    private lateinit var binding: ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding =
            ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return imagenesSeleccionadas.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {

        val modelo = imagenesSeleccionadas[position]

        if (modelo.deInternet) {
            try {

                val imagenUrl = modelo.imagenUrl

                Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.item_imagen)
                    .into(binding.itemImagen)

            } catch (e: Exception) {

            }
        } else {
            try {

                val imagenUri = modelo.imagenUri

                Glide.with(context)
                    .load(imagenUri)
                    .placeholder(R.drawable.item_imagen)
                    .into(holder.item_imagen)

            } catch (e: Exception) {

            }
        }

        holder.btn_cerrar.setOnClickListener {
            if (modelo.deInternet) {
                val btn_si: MaterialButton
                val btn_no: MaterialButton
                val dialog = Dialog(context)

                dialog.setContentView(R.layout.cuadro_eliminar_imagen)

                btn_si = dialog.findViewById(R.id.btn_si)
                btn_no = dialog.findViewById(R.id.btn_no)

                btn_si.setOnClickListener {
                    eliminarImgFirebase(modelo, holder, position)
                    dialog.dismiss()
                }

                btn_no.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
                dialog.setCanceledOnTouchOutside(false)

            } else {
                imagenesSeleccionadas.remove(modelo)
                notifyDataSetChanged()
            }
        }
    }

    private fun eliminarImgFirebase(
        modelo: ImagenSeleccionadaModel,
        holder: HolderImagenSeleccionada,
        position: Int
    ) {
        val idImagen = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")

        ref
            .child(idAnuncio)
            .child("Imagenes")
            .child(idImagen)
            .removeValue()
            .addOnSuccessListener {
                try {
                    imagenesSeleccionadas.remove(modelo)
                    eliminarImgStorage(modelo)
                    notifyDataSetChanged()
                } catch (e: Exception) {

                }
            }.addOnFailureListener {
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarImgStorage(modelo: ImagenSeleccionadaModel) {

        val rutaImagen = "Anuncios/" + modelo.id

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)

        ref
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "La imagen se ha eliminado", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    inner class HolderImagenSeleccionada(itemView: View) : ViewHolder(itemView) {

        var item_imagen = binding.itemImagen
        var btn_cerrar = binding.cerrarItem

    }

}