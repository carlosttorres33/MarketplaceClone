package com.carlostorres.comprayventa.detalle_vendedor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.adapters.AnuncioAdapter
import com.carlostorres.comprayventa.databinding.ActivityDetalleVendedorBinding
import com.carlostorres.comprayventa.model.AnuncioModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleVendedorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetalleVendedorBinding

    private var uidVendedor = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uidVendedor = intent.getStringExtra("uidVendedor").toString()

        cargarInfoVendedor()
        cargarAnunciosVendedor()

    }

    private fun cargarAnunciosVendedor(){

        val anuncioArrayList : ArrayList<AnuncioModel> = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.orderByChild("uid").equalTo(uidVendedor)
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        anuncioArrayList.clear()
                        for (ds in snapshot.children){
                            try {
                                val modeloAnuncio = ds.getValue(AnuncioModel::class.java)
                                anuncioArrayList.add(modeloAnuncio!!)
                            }catch (e:Exception){}
                        }

                        val adaptador = AnuncioAdapter(this@DetalleVendedorActivity, anuncioArrayList)

                        binding.anunciosRV.adapter = adaptador

                        val contadorAnuncios = anuncioArrayList.size
                        binding.tvNoAnuncios.text = contadorAnuncios.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }

    private fun cargarInfoVendedor(){

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")

        ref.child(uidVendedor)
            .addValueEventListener(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nombres = "${snapshot.child("nombres").value}"
                        val imgPerfil = "${snapshot.child("urlImagenPerfil").value}"
                        val tiempo = snapshot.child("tiempo").value as Long

                        val f_fecha = Constantes.obtenerFecha(tiempo)

                        binding.tvNombres.text = nombres
                        binding.tvMiembro.text = f_fecha

                        try {
                            Glide.with(this@DetalleVendedorActivity)
                                .load(imgPerfil)
                                .placeholder(R.drawable.item_imagen)
                                .into(binding.ivVendedor)
                        }catch (e:Exception){

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }
}