package com.carlostorres.comprayventa

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

object Constantes {

    const val ANUNCIO_DISPONIBLE = "Disponible"
    const val ANUNCIO_VENDIDO = "Vendido"

    val categorias = arrayOf(
        "Moviles",
        "Ordenadores/Laptops",
        "Electronica y Electrodomesticos",
        "Vehiculos",
        "Consolas",
        "Hogar y Muebles",
        "Belleza y Cuidado personal",
        "Libros",
        "Deportes"
    )

    val condiciones = arrayOf(
        "Nuevo",
        "Usado",
        "Renovado"
    )

    fun obtenerTiempoDis() : Long {
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo: Long):String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }

}