package com.carlostorres.comprayventa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.carlostorres.comprayventa.databinding.ActivityLoginEmailBinding
import com.carlostorres.comprayventa.databinding.ActivityOpcionesLoginBinding
import com.carlostorres.comprayventa.opciones_login.LoginEmail
import com.google.firebase.auth.FirebaseAuth

class OpcionesLogin : AppCompatActivity() {

    private lateinit var binding : ActivityOpcionesLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        comprobarSesion()

        binding.mbIngresarEmail.setOnClickListener {

            startActivity(Intent(this@OpcionesLogin, LoginEmail::class.java))

        }


    }

    private fun comprobarSesion(){
        if (firebaseAuth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

}