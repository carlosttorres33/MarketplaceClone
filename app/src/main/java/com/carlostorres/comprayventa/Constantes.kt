package com.carlostorres.comprayventa

import android.text.format.DateFormat
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

}