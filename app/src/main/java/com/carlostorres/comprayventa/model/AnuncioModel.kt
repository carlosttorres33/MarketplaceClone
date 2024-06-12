package com.carlostorres.comprayventa.model

class AnuncioModel{
    var id : String = ""
    var uid : String = ""
    var marca : String = ""
    var categoria : String = ""
    var condicion : String = ""
    var direccion : String = ""
    var precio : String = ""
    var titulo : String = ""
    var descripcion : String = ""
    var estado : String = ""
    var tiempo : Long = 0
    var latitud : Double = 0.0
    var longitud : Double = 0.0
    var favorito : Boolean = false

    constructor()
    constructor(
        id: String,
        uid: String,
        marca: String,
        categoria: String,
        condicion: String,
        direccion: String,
        precio: String,
        titulo: String,
        descripcion: String,
        estado: String,
        tiempo: Long,
        latitud: Double,
        longitud: Double,
        favorito: Boolean
    ) {
        this.id = id
        this.uid = uid
        this.marca = marca
        this.categoria = categoria
        this.condicion = condicion
        this.direccion = direccion
        this.precio = precio
        this.titulo = titulo
        this.descripcion = descripcion
        this.estado = estado
        this.tiempo = tiempo
        this.latitud = latitud
        this.longitud = longitud
        this.favorito = favorito
    }


}