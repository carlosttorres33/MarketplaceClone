package com.carlostorres.comprayventa.detalle_anuncio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.MainActivity
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.adapters.ImgSliderAdapter
import com.carlostorres.comprayventa.databinding.ActivityDetalleAnuncioBinding
import com.carlostorres.comprayventa.model.AnuncioModel
import com.carlostorres.comprayventa.model.ImagenSeleccionadaModel
import com.carlostorres.comprayventa.model.ImgSliderModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleAnuncioBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private var idAnuncio = ""

    private var anuncioLatitud = 0.0
    private var anuncioLongitud = 0.0

    private var uidVendedor = ""
    private var telVendedor = ""

    private var favorito = false

    private lateinit var imagenSliderArrayList: ArrayList<ImgSliderModel>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetalleAnuncioBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        idAnuncio = intent.getStringExtra("idAnuncio").toString()

        binding.ibFav.setOnClickListener {
            if (favorito){
                Constantes.eliminarAnuncioFav(this, idAnuncio)
            }else{
                Constantes.agregarAnuncioFav(this, idAnuncio)
            }
        }

        binding.ibRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        comprobarAnuncioFav()
        cargarInfoAnuncio()
        cargarImgsAnuncio()

        binding.ibEliminar.setOnClickListener {
            val mAlertDialog = MaterialAlertDialogBuilder(this)
            mAlertDialog.setTitle("Eliminar anuncio")
                .setMessage("Seguro?")
                .setPositiveButton("Eliminar"){ dialog, which ->
                    eliminarAnuncio()
                }
                .setNegativeButton("Cancelar"){ dialog, which ->
                    dialog.dismiss()
                }.show()
        }

        binding.btnMapa.setOnClickListener {
            Constantes.mapaIntent(this, anuncioLatitud, anuncioLongitud)
        }

        binding.btnLlamar.setOnClickListener {
            val numeroTel = telVendedor
            if (numeroTel.isEmpty()){
                Toast.makeText(this@DetalleAnuncio, "El vendedor no tiene numero", Toast.LENGTH_SHORT).show()
            }else{
                Constantes.llamarIntent(this, numeroTel)
            }
        }

    }

    private fun cargarInfoAnuncio(){

        var refDatabase = FirebaseDatabase.getInstance().getReference("Anuncios")

        refDatabase
            .child(idAnuncio)
            .addValueEventListener(
                object  : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val modeloAnuncio = snapshot.getValue(AnuncioModel::class.java)

                            uidVendedor = "${modeloAnuncio!!.uid}"
                            val titulo = modeloAnuncio.titulo
                            val descripcion = modeloAnuncio.descripcion
                            val direccion = modeloAnuncio.direccion
                            val condicion = modeloAnuncio.condicion
                            val categoria = modeloAnuncio.categoria
                            val precio = modeloAnuncio.precio
                            anuncioLatitud = modeloAnuncio.latitud
                            anuncioLongitud = modeloAnuncio.longitud
                            val tiempo = modeloAnuncio.tiempo

                            val formatoFecha = Constantes.obtenerFecha(tiempo)

                            if (uidVendedor == firebaseAuth.uid){

                                binding.btnMapa.visibility = View.GONE
                                binding.btnLlamar.visibility = View.GONE
                                binding.btnSms.visibility = View.GONE
                                binding.btnChat.visibility = View.GONE

                                binding.ibEditar.visibility = View.VISIBLE
                                binding.ibEliminar.visibility = View.VISIBLE

                            }else{

                                binding.ibEditar.visibility = View.GONE
                                binding.ibEliminar.visibility = View.GONE

                                binding.btnMapa.visibility = View.VISIBLE
                                binding.btnLlamar.visibility = View.VISIBLE
                                binding.btnSms.visibility = View.VISIBLE
                                binding.btnChat.visibility = View.VISIBLE

                            }

                            binding.tvTitulo.text = titulo
                            binding.tvDescr.text = descripcion
                            binding.tvDireccion.text = direccion
                            binding.tvCondicion.text = condicion
                            binding.tvCat.text = categoria
                            binding.tvPrecio.text = precio
                            binding.tvFecha.text = formatoFecha

                            //Info Vendedor
                            cargarInfoVendedor()

                        }catch (e:Exception){

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }

    private fun cargarInfoVendedor() {

        val refDatabase = FirebaseDatabase.getInstance().getReference("Usuarios")

        refDatabase
            .child(uidVendedor)
            .addValueEventListener(
                object :ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val telefono = "${snapshot.child("telefono").value}"
                        val codTel = "${snapshot.child("codigoTelefono").value}"
                        val nombre = "${snapshot.child("nombres").value}"
                        val imgPerfil = "${snapshot.child("urlImagenPerfil").value}"
                        val tiempoReg = snapshot.child("tiempo").value as Long

                        val formatoFecha = Constantes.obtenerFecha(tiempoReg)

                        telVendedor =  "$codTel$telefono"

                        binding.tvNombres.text = nombre
                        binding.tvMiembro.text = formatoFecha

                        try {
                            Glide
                                .with(this@DetalleAnuncio)
                                .load(imgPerfil)
                                .placeholder(R.drawable.img_perfil)
                                .into(binding.imgPerfil)
                        }catch (e:Exception){

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }

    private fun cargarImgsAnuncio(){

        imagenSliderArrayList = ArrayList()

        val refDatabase = FirebaseDatabase.getInstance().getReference("Anuncios")

        refDatabase
            .child(idAnuncio)
            .child("Imagenes")
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        imagenSliderArrayList.clear()
                        for (ds in snapshot.children){
                            try {
                                val modeloImagenSlider = ds.getValue(ImgSliderModel::class.java)
                                imagenSliderArrayList.add(modeloImagenSlider!!)
                            }catch (e:Exception){}
                        }

                        val adapadorImgSlider = ImgSliderAdapter(this@DetalleAnuncio, imagenSliderArrayList)
                        binding.imgSliderVp.adapter = adapadorImgSlider

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }

    private fun comprobarAnuncioFav(){

        val refDatabase = FirebaseDatabase.getInstance().getReference("Usuarios")
        refDatabase
            .child("${firebaseAuth.uid}").child("Favoritos").child(idAnuncio)
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        favorito = snapshot.exists()

                        if (favorito){
                            binding.ibFav.setImageResource(R.drawable.iv_fav)
                        }else{
                            binding.ibFav.setImageResource(R.drawable.ic_notfav)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }

    private fun eliminarAnuncio(){

        val refDatabase = FirebaseDatabase.getInstance().getReference("Anuncios")

        refDatabase
            .child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                startActivity(Intent(this@DetalleAnuncio, MainActivity::class.java))
                finishAffinity()
                Toast.makeText(this, "Anuncio Eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

}
