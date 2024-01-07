package com.carlostorres.comprayventa.fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.OpcionesLogin
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class FragmentCuenta : Fragment() {

    private lateinit var binding : FragmentCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        leerInfo()

        binding.btnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, OpcionesLogin::class.java))
            activity?.finishAffinity()
        }

    }

    private fun leerInfo() {
        var ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val fNacimiento = "${snapshot.child("fecha_nac").value}"
                    var tiempo = "${snapshot.child("tiempo").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codigoTelefono = "${snapshot.child("codigoTelefono").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"

                    val codigoTelefonoCompleto = codigoTelefono+telefono

                    if (tiempo == null){
                        tiempo = "0"
                    }

                    val formatoTiempo = Constantes.obtenerFecha(tiempo.toLong())

                    //Seter de info
                    binding.tvEmail.text = email
                    binding.tvEmail.text = nombres
                    binding.tvNacmimento.text = fNacimiento
                    binding.tvTelefono.text = codigoTelefonoCompleto
                    binding.tvMiembro.text = formatoTiempo

                    //Seteo imagen
                    try {

                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.ivPerfil)

                    }catch (e: Exception){
                        Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
                    }

                    if(proveedor == "email"){
                        val esVer = firebaseAuth.currentUser!!.isEmailVerified
                        if (esVer){
                            binding.tvEstadoCuenta.text = "Verificado"
                        }else{
                            binding.tvEstadoCuenta.text = "No Verificado"
                        }
                    }else{
                        binding.tvEstadoCuenta.text = "Verificado"
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}