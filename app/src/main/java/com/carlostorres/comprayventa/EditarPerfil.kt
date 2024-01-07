package com.carlostorres.comprayventa

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditarPerfil : AppCompatActivity() {

    private lateinit var binding : ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere Por Favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarInfo()

    }

    private fun cargarInfo() {

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val fechaNacimiento = "${snapshot.child("fecha_nac").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"

                    binding.etNombres.setText(nombres)
                    binding.etFechaNac.setText(fechaNacimiento)
                    binding.etTelefono.setText(telefono)

                    try {

                        Glide.with(applicationContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.ivImgPerfil)

                    }catch (e: Exception){
                        Toast.makeText(this@EditarPerfil, e.message, Toast.LENGTH_SHORT).show()
                    }

                    try {
                        val codigo = codTelefono.replace("+","").toInt()
                        binding.codeSelector.setCountryForPhoneCode(codigo)
                    }catch (e: java.lang.Exception){
                        Toast.makeText(this@EditarPerfil, e.message, Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}