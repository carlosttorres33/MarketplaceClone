package com.carlostorres.comprayventa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.carlostorres.comprayventa.databinding.ActivityLoginEmailBinding
import com.carlostorres.comprayventa.databinding.ActivityOpcionesLoginBinding
import com.carlostorres.comprayventa.opciones_login.LoginEmail

class OpcionesLogin : AppCompatActivity() {

    private lateinit var binding : ActivityOpcionesLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mbIngresarEmail.setOnClickListener {

            startActivity(Intent(this@OpcionesLogin, LoginEmail::class.java))

        }

    }

}