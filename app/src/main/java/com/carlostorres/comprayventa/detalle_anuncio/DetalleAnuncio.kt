package com.carlostorres.comprayventa.detalle_anuncio

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.MainActivity
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.adapters.ImgSliderAdapter
import com.carlostorres.comprayventa.anuncios.CrearAnuncioActivity
import com.carlostorres.comprayventa.databinding.ActivityDetalleAnuncioBinding
import com.carlostorres.comprayventa.detalle_vendedor.DetalleVendedorActivity
import com.carlostorres.comprayventa.model.AnuncioModel
import com.carlostorres.comprayventa.model.ImgSliderModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleAnuncio : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAnuncioBinding
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

            if (favorito) {

                Constantes.eliminarAnuncioFav(this, idAnuncio)

            } else {

                Constantes.agregarAnuncioFav(this, idAnuncio)

            }
        }

        binding.ibRegresar.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()

        }

        comprobarAnuncioFav()
        cargarInfoAnuncio()
        cargarImgsAnuncio()

        binding.ibEditar.setOnClickListener {
            opcionesDialog()
        }

        binding.ibEliminar.setOnClickListener {

            val mAlertDialog = MaterialAlertDialogBuilder(this)

            mAlertDialog.setTitle("Eliminar anuncio")
                .setMessage("Seguro?")
                .setPositiveButton("Eliminar") { dialog, which ->

                    eliminarAnuncio()

                }.setNegativeButton("Cancelar") { dialog, which ->

                    dialog.dismiss()

                }.show()

        }

        binding.btnMapa.setOnClickListener {

            Constantes.mapaIntent(this, anuncioLatitud, anuncioLongitud)

        }

        binding.btnLlamar.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val numeroTel = telVendedor

                if (numeroTel.isEmpty()) {

                    Toast.makeText(
                        this@DetalleAnuncio,
                        "El vendedor no tiene numero",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Constantes.llamarIntent(this, numeroTel)

                }
            } else {

                permisoLlamada.launch(android.Manifest.permission.CALL_PHONE)

            }
        }

        binding.btnSms.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val numTel = telVendedor

                if (numTel.isEmpty()) {

                    Toast.makeText(
                        this@DetalleAnuncio,
                        "El vendedor no tiene numero",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Constantes.smsIntent(this, telVendedor)

                }
            } else {

                permisoSms.launch(Manifest.permission.SEND_SMS)

            }
        }

        binding.ivInfoVendedor.setOnClickListener {
            val intent = Intent(this, DetalleVendedorActivity::class.java)
            intent.putExtra("uidVendedor", uidVendedor)

            startActivity(intent)
        }
    }

    private fun opcionesDialog() {

        val popupMenu = PopupMenu(this, binding.ibEditar)

        popupMenu.menu.add(Menu.NONE, 0,0, "Editar")
        popupMenu.menu.add(Menu.NONE, 1,1, "Marcar como vendido")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {  item ->

            val itemId = item.itemId

            if (itemId == 0){

                //Editar
                val intent = Intent(this, CrearAnuncioActivity::class.java)
                intent.putExtra("Edicion", true)
                intent.putExtra("idAnuncio", idAnuncio)

                startActivity(intent)

            }else if(itemId == 1){
                //Vendido
                dialogMarcarVendido()
            }

            return@setOnMenuItemClickListener true

        }

    }

    //region CargarInformacionAnuncio
    private fun cargarInfoAnuncio() {

        var refDatabase = FirebaseDatabase.getInstance().getReference("Anuncios")

        refDatabase
            .child(idAnuncio)
            .addValueEventListener(
                object : ValueEventListener {
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
                            val estado = modeloAnuncio.estado
                            anuncioLatitud = modeloAnuncio.latitud
                            anuncioLongitud = modeloAnuncio.longitud
                            val tiempo = modeloAnuncio.tiempo

                            val formatoFecha = Constantes.obtenerFecha(tiempo)

                            if (uidVendedor == firebaseAuth.uid) {

                                binding.btnMapa.visibility = View.GONE
                                binding.btnLlamar.visibility = View.GONE
                                binding.btnSms.visibility = View.GONE
                                binding.btnChat.visibility = View.GONE

                                binding.txtDescrVendedor.visibility = View.GONE
                                binding.perfilVendedor.visibility = View.GONE

                                binding.ibEditar.visibility = View.VISIBLE
                                binding.ibEliminar.visibility = View.VISIBLE

                            } else {

                                binding.ibEditar.visibility = View.GONE
                                binding.ibEliminar.visibility = View.GONE

                                binding.btnMapa.visibility = View.VISIBLE
                                binding.btnLlamar.visibility = View.VISIBLE
                                binding.btnSms.visibility = View.VISIBLE
                                binding.btnChat.visibility = View.VISIBLE

                                binding.txtDescrVendedor.visibility = View.VISIBLE
                                binding.perfilVendedor.visibility = View.VISIBLE

                            }

                            binding.tvTitulo.text = titulo
                            binding.tvDescr.text = descripcion
                            binding.tvDireccion.text = direccion
                            binding.tvCondicion.text = condicion
                            binding.tvCat.text = categoria
                            binding.tvPrecio.text = precio
                            binding.tvFecha.text = formatoFecha
                            binding.tvEstado.text = estado

                            if (estado.equals("Disponible")){
                                binding.tvEstado.setTextColor(Color.BLUE)
                            }else{
                                binding.tvEstado.setTextColor(Color.RED)
                            }

                            //Info Vendedor
                            cargarInfoVendedor()

                        } catch (e: Exception) {

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
            )
    }
    //endregion

    private fun marcarAnuncioVendido(){

        val hashMap = HashMap<String, Any>()
        hashMap["estado"] = Constantes.ANUNCIO_VENDIDO
        
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.child(idAnuncio)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Anuncio marcado como vendido", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al marcar como vendido ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dialogMarcarVendido(){
        val btnSi : MaterialButton
        val btnNo : MaterialButton
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.marcar_vendido_dialog)

        btnSi = dialog.findViewById(R.id.btn_si)
        btnNo = dialog.findViewById(R.id.btn_no)

        btnSi.setOnClickListener {
            marcarAnuncioVendido()
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    //region cargarInfoVendedor
    private fun cargarInfoVendedor() {

        val refDatabase = FirebaseDatabase.getInstance().getReference("Usuarios")

        refDatabase
            .child(uidVendedor)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val telefono = "${snapshot.child("telefono").value}"
                        val codTel = "${snapshot.child("codigoTelefono").value}"
                        val nombre = "${snapshot.child("nombres").value}"
                        val imgPerfil = "${snapshot.child("urlImagenPerfil").value}"
                        val tiempoReg = snapshot.child("tiempo").value as Long

                        val formatoFecha = Constantes.obtenerFecha(tiempoReg)

                        telVendedor = "$codTel$telefono"

                        binding.tvNombres.text = nombre
                        binding.tvMiembro.text = formatoFecha

                        try {
                            Glide
                                .with(this@DetalleAnuncio)
                                .load(imgPerfil)
                                .placeholder(R.drawable.img_perfil)
                                .into(binding.imgPerfil)
                        } catch (e: Exception) {

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
            )
    }
    //endregion

    //region cargarImagenesDelAnuncio
    private fun cargarImgsAnuncio() {

        imagenSliderArrayList = ArrayList()

        val refDatabase = FirebaseDatabase.getInstance().getReference("Anuncios")

        refDatabase
            .child(idAnuncio)
            .child("Imagenes")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        imagenSliderArrayList.clear()
                        for (ds in snapshot.children) {
                            try {
                                val modeloImagenSlider = ds.getValue(ImgSliderModel::class.java)
                                imagenSliderArrayList.add(modeloImagenSlider!!)
                            } catch (e: Exception) {
                            }
                        }

                        val adapadorImgSlider =
                            ImgSliderAdapter(this@DetalleAnuncio, imagenSliderArrayList)
                        binding.imgSliderVp.adapter = adapadorImgSlider

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
            )
    }
    //endregion

    //region comprobarSiAnuncioEsFavorito
    private fun comprobarAnuncioFav() {

        val refDatabase = FirebaseDatabase.getInstance().getReference("Usuarios")
        refDatabase
            .child("${firebaseAuth.uid}").child("Favoritos").child(idAnuncio)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        favorito = snapshot.exists()

                        if (favorito) {
                            binding.ibFav.setImageResource(R.drawable.iv_fav)
                        } else {
                            binding.ibFav.setImageResource(R.drawable.ic_notfav)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
            )
    }
    //endregion

    //region eliminarAnuncio
    private fun eliminarAnuncio() {

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
    //endregion

    //region permisoLlamada
    private val permisoLlamada = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { conceder ->
        if (conceder) {
            val numeroTel = telVendedor
            if (numeroTel.isEmpty()) {
                Toast.makeText(
                    this@DetalleAnuncio,
                    "El vendedor no tiene numero",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Constantes.llamarIntent(this, numeroTel)
            }
        } else {
            Toast.makeText(this@DetalleAnuncio, "Permiso Denegado", Toast.LENGTH_SHORT).show()
        }
    }
    //endregion

    //region permisoSMS
    private val permisoSms =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { conceder ->
            if (conceder) {
                val numTel = telVendedor
                if (numTel.isEmpty()) {
                    Toast.makeText(
                        this@DetalleAnuncio,
                        "El vendedor no tiene numero",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Constantes.smsIntent(this, telVendedor)
                }
            } else {
                Toast.makeText(
                    this@DetalleAnuncio,
                    "Permiso Denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    //endregion

}
