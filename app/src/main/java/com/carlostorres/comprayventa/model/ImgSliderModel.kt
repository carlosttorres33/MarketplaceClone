package com.carlostorres.comprayventa.model

class ImgSliderModel {

    var id : String = ""
    var imagenUrl : String = ""

    constructor()
    constructor(id: String, imgUrl: String) {
        this.id = id
        this.imagenUrl = imgUrl
    }


}