package com.carlostorres.comprayventa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

object Constantes {

    const val ANUNCIO_DISPONIBLE = "Disponible"
    const val ANUNCIO_VENDIDO = "Vendido"

    val categorias = arrayOf(
        "Todos",
        "Moviles",
        "Ordenadores/Laptops",
        "Electronica y Electrodomesticos",
        "Vehiculos",
        "Consolas",
        "Hogar y Muebles",
        "Belleza y Cuidado personal",
        "Libros",
        "Deportes",
        "Juguetes y Figuras",
        "Mascotas"
    )

    val condiciones = arrayOf(
        "Nuevo",
        "Usado",
        "Renovado"
    )

    val categoriasIcono = arrayOf(
        R.drawable.ic_categoria_todos,
        R.drawable.ic_categoria_moviles,
        R.drawable.ic_categoria_ordenadores,
        R.drawable.ic_categoria_electrodomesticos,
        R.drawable.ic_categorias_vehiculos,
        R.drawable.ic_categorias_consolas,
        R.drawable.ic_categoria_muebles,
        R.drawable.ic_categoria_belleza,
        R.drawable.ic_categoria_libros,
        R.drawable.ic_categoria_deportes,
        R.drawable.ic_categoria_juguetes,
        R.drawable.ic_categoria_mascotas,
    )

    fun obtenerTiempoDis() : Long {
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo: Long):String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }

    fun agregarAnuncioFav(context : Context, idAnuncio : String){

        val firebasAuth = FirebaseAuth.getInstance()
        val tiempo = obtenerTiempoDis()

        val hashMap = HashMap<String, Any>()
        hashMap["idAnuncio"] = idAnuncio
        hashMap["tiempo"] = tiempo

        val refDatabase = FirebaseDatabase
            .getInstance()
            .getReference("Usuarios")

        refDatabase
            .child(firebasAuth.uid!!)
            .child("Favoritos")
            .child(idAnuncio)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(context, "Anuncio Agregado a Favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    fun eliminarAnuncioFav(context : Context, idAnuncio : String){

        val firebaseAuth = FirebaseAuth.getInstance()

        val refDatabase = FirebaseDatabase
            .getInstance()
            .getReference("Usuarios")
        refDatabase
            .child(firebaseAuth.uid!!)
            .child("Favoritos")
            .child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Eliminado de Favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {  e ->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    fun mapaIntent(context: Context, latitud : Double, longitud : Double){

        val googleMapIntentUri = Uri.parse("http://maps.google.com/maps?daddr=$latitud,$longitud")
        val mapIntent = Intent(Intent.ACTION_VIEW, googleMapIntentUri)

        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null){
            context.startActivity(mapIntent)
        }else{
            Toast.makeText(context, "No tienes instalado GoogleMaps", Toast.LENGTH_SHORT).show()
        }

    }

    fun llamarIntent (context: Context, telefono : String){

        val intent = Intent(Intent.ACTION_CALL)
        intent.setData(Uri.parse("tel:$telefono"))

        context.startActivity(intent)

    }

    fun smsIntent(context: Context, telefono: String){

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("smsto:$telefono"))
        intent.putExtra("sms_body", "")

        context.startActivity(intent)

    }

}