package com.carlostorres.comprayventa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.carlostorres.comprayventa.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroEmail : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroEmailBinding

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favot")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrar.setOnClickListener {

            validarInfo()

        }

    }

    private var email = ""
    private var password = ""
    private var repetirPassword = ""

    private fun validarInfo() {

        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        repetirPassword = binding.etRPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email invalido"
            binding.etEmail.requestFocus()
        } else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese Email"
            binding.etEmail.requestFocus()
        }else if(password.isEmpty()){
            binding.etPassword.error = "Ingrese Password"
            binding.etPassword.requestFocus()
        }else if(repetirPassword.isEmpty()){
            binding.etRPassword.error = "Repita Password"
            binding.etRPassword.requestFocus()
        }else if (password != repetirPassword){
            binding.etRPassword.error = "No coinciden"
            binding.etRPassword.requestFocus()
        } else {
            registrarUsuario()
        }

    }

    private fun registrarUsuario() {

        progressDialog.setMessage("Creando Cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                llenarInfoBD()
            }
            .addOnFailureListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, "No se registró el usuario: $error", Toast.LENGTH_SHORT).show()
            }


    }

    private fun llenarInfoBD() {

        progressDialog.setMessage("Guardando Info")

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser!!.email

        val uidUsuario = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = ""
        hashMap["codigoTelefono"] = ""
        hashMap["telefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "email"
        hashMap["escribiendo"] = "email"
        hashMap["tiempo"] = tiempo
        hashMap["online"] = true
        hashMap["email"] = "${emailUsuario}"
        hashMap["uid"] = "${uidUsuario}"
        hashMap["fecha_nac"] = ""

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "No se registró debido a $it", Toast.LENGTH_SHORT).show()
            }




    }

}