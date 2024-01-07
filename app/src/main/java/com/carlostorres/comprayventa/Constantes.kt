package com.carlostorres.comprayventa

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

object Constantes {

    fun obtenerTiempoDis() : Long {
        return System.currentTimeMillis()
    }

    fun obtenerFecha(tiempo: Long):String{
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }

}