package com.carlostorres.comprayventa.opciones_login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.carlostorres.comprayventa.MainActivity
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.RegistroEmail
import com.carlostorres.comprayventa.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmail : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progresDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progresDialog = ProgressDialog(this)
        progresDialog.setTitle("Espere por favor")
        progresDialog.setCanceledOnTouchOutside(false)

        binding.btnIngresar.setOnClickListener {
            validarInfo()
        }

        binding.txtRegistrarme.setOnClickListener {

            startActivity(Intent(this@LoginEmail, RegistroEmail::class.java))

        }

    }

    private var email = ""
    private var password = ""

    private fun validarInfo() {

        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email Inválido"
            binding.etEmail.requestFocus()
        } else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese Email"
            binding.etEmail.requestFocus()
        } else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese Password"
            binding.etPassword.requestFocus()
        } else {
            loginUsuario()
        }

    }

    private fun loginUsuario() {

        progresDialog.setMessage("Iniciando Sesión")

        progresDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                progresDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
                Toast.makeText(this, "Bienvenid@", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                progresDialog.dismiss()
                Toast.makeText(this, "No se pudo iniciar sesión debido a $e", Toast.LENGTH_SHORT).show()

            }
    }

}