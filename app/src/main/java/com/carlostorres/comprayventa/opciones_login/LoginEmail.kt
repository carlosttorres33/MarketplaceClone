package com.carlostorres.comprayventa.opciones_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.RegistroEmail
import com.carlostorres.comprayventa.databinding.ActivityLoginEmailBinding

class LoginEmail : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtRegistrarme.setOnClickListener {

            startActivity(Intent(this@LoginEmail, RegistroEmail::class.java))

        }

    }

}