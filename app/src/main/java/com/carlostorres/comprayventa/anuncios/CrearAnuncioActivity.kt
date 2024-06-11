package com.carlostorres.comprayventa.anuncios

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.SeleccionarUbicacionActivity
import com.carlostorres.comprayventa.adapters.ImagenSeleccionadaAdapter
import com.carlostorres.comprayventa.databinding.ActivityCrearAnuncioBinding
import com.carlostorres.comprayventa.model.ImagenSeleccionadaModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CrearAnuncioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearAnuncioBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var imagenUri: Uri? = null

    private lateinit var imagenesSeleccionadas: ArrayList<ImagenSeleccionadaModel>
    private lateinit var imagenSeleccionadaAdapter: ImagenSeleccionadaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearAnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val categoriasAdapter = ArrayAdapter(this, R.layout.item_categoria, Constantes.categorias)
        binding.actvCategoria.setAdapter(categoriasAdapter)

        val condicionAdapter = ArrayAdapter(this, R.layout.item_condicion, Constantes.condiciones)
        binding.actvCondicion.setAdapter(condicionAdapter)

        imagenesSeleccionadas = ArrayList()
        cargarImagenes()

        binding.ivAgregarImagen.setOnClickListener {
            mostrarOpciones()
        }

        binding.actvLocacion.setOnClickListener {
            val i = Intent(this, SeleccionarUbicacionActivity::class.java)
            seleccionarUbicacion_ARL.launch(i)
        }

        binding.btnCrearAnuncio.setOnClickListener {
            validarDatos()
        }

    }

    private fun limpiarCampos() {
        imagenesSeleccionadas.clear()
        imagenSeleccionadaAdapter.notifyDataSetChanged()
        binding.etMarca.setText("")
        binding.actvCategoria.setText("")
        binding.actvCondicion.setText("")
        binding.actvLocacion.setText("")
        binding.etPrecio.setText("")
        binding.etTitulo.setText("")
        binding.etDescripcion.setText("")
    }

    private fun cargarImagenes() {
        imagenSeleccionadaAdapter = ImagenSeleccionadaAdapter(this, imagenesSeleccionadas)
        binding.rvImagenes.adapter = imagenSeleccionadaAdapter
    }

    private fun mostrarOpciones() {

        val popupMenu = PopupMenu(this, binding.ivAgregarImagen)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galeria")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->

            val itemId = item.itemId

            if (itemId == 1) {

                if (Build.VERSION.SDK_INT > -Build.VERSION_CODES.TIRAMISU) {
                    solicitarPermisoCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                } else {
                    solicitarPermisoCamara.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }

            } else if (itemId == 2) {

                if (Build.VERSION.SDK_INT > -Build.VERSION_CODES.TIRAMISU) {
                    imagenGaleria()
                } else {
                    solicitarPermisoAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

            }
            true

        }

    }

    private val solicitarPermisoAlmacenamiento = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { esConcedido ->

        if (esConcedido) {
            imagenGaleria()
        } else {
            Toast.makeText(this, "Permisos Denegados", Toast.LENGTH_SHORT).show()
        }

    }

    private fun imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)
    }

    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data
                imagenUri = data!!.data

                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImgSel = ImagenSeleccionadaModel(
                    tiempo,
                    imagenUri,
                    null,
                    false
                )

                imagenesSeleccionadas.add(modeloImgSel)

                cargarImagenes()

            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun imagenCamara() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_Imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_Imagen")

        imagenUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)

        resultadoCamara_ARL.launch(intent)

    }

    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val tiempo = "${Constantes.obtenerTiempoDis()}"
                val modeloImgSel = ImagenSeleccionadaModel(
                    tiempo,
                    imagenUri,
                    null,
                    false
                )
                imagenesSeleccionadas.add(modeloImgSel)
                cargarImagenes()
            } else {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }

    private val solicitarPermisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        var todosConcedidos = true

        for (esConcedido in result.values) {
            todosConcedidos = todosConcedidos && esConcedido
        }

        if (todosConcedidos) {
            imagenCamara()
        } else {
            Toast.makeText(this, "Permisos Denegados", Toast.LENGTH_SHORT).show()
        }
    }

    private var marca = ""
    private var categoria = ""
    private var condicion = ""
    private var direccion = ""
    private var precio = ""
    private var titulo = ""
    private var descripcion = ""
    private var latitud = 0.0
    private var longitud = 0.0

    private fun validarDatos() {

        marca = binding.etMarca.text.toString().trim()
        categoria = binding.actvCategoria.text.toString().trim()
        condicion = binding.actvCondicion.text.toString().trim()
        direccion = binding.actvLocacion.text.toString().trim()
        precio = binding.etPrecio.text.toString().trim()
        titulo = binding.etTitulo.text.toString().trim()
        descripcion = binding.etDescripcion.text.toString().trim()

        if (marca.isEmpty()) {
            binding.etMarca.error = "Ingresa Marca"
            binding.etMarca.requestFocus()
        } else if (categoria.isEmpty()) {
            binding.actvCategoria.error = "Ingresa Categoria"
            binding.actvCategoria.requestFocus()
        } else if (condicion.isEmpty()) {
            binding.actvCondicion.error = "Ingresa Condicion"
            binding.actvCondicion.requestFocus()
        } else if (precio.isEmpty()) {
            binding.etPrecio.error = "Ingresa Precio"
            binding.etPrecio.requestFocus()
        } else if (titulo.isEmpty()) {
            binding.etTitulo.error = "Ingresa Direccion"
            binding.etTitulo.requestFocus()
        } else if (descripcion.isEmpty()) {
            binding.etDescripcion.error = "Ingresa Descripcion"
            binding.etDescripcion.requestFocus()
        }else if (direccion.isEmpty()){
            binding.actvLocacion.error = "Ingresa ubicacion"
            binding.actvLocacion.requestFocus()
        }else if (imagenUri == null) {
            Toast.makeText(this, "Agrega al menos una imagen", Toast.LENGTH_SHORT).show()
        } else {
            agregarAnuncio()
        }


    }

    private val seleccionarUbicacion_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                if (data != null) {
                    latitud = data.getDoubleExtra("latitud", 0.0)
                    longitud = data.getDoubleExtra("longitud", 0.0)
                    direccion = data.getStringExtra("direccion") ?: ""

                    binding.actvLocacion.setText(direccion)
                }
            }else{
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun agregarAnuncio() {
        progressDialog.setMessage("Agregando Anuncio")
        progressDialog.show()

        val tiempo = Constantes.obtenerTiempoDis()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = keyId.toString()
        hashMap["uid"] = firebaseAuth.uid.toString()
        hashMap["marca"] = marca
        hashMap["categoria"] = categoria
        hashMap["condicion"] = condicion
        hashMap["direccion"] = direccion
        hashMap["precio"] = precio
        hashMap["titulo"] = titulo
        hashMap["descripcion"] = descripcion
        hashMap["estado"] = Constantes.ANUNCIO_DISPONIBLE
        hashMap["tiempo"] = tiempo
        hashMap["latitud"] = latitud
        hashMap["longitud"] = longitud

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                cargarImagenesStorage(keyId)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun cargarImagenesStorage(keyId: String) {
        for (i in imagenesSeleccionadas.indices) {
            val modeloImg = imagenesSeleccionadas[i]
            val nombreImg = modeloImg.id
            val rutaNombreImagen = "Anuncios/$nombreImg"
            val storageRef = FirebaseStorage.getInstance().getReference(rutaNombreImagen)
            storageRef.putFile(modeloImg.imagenUri!!)
                .addOnSuccessListener {
                    val uriTask = it.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImgCargada = uriTask.result

                    if (uriTask.isSuccessful) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = modeloImg.imagenUri.toString()
                        hashMap["imagenUrl"] = urlImgCargada

                        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
                        ref.child(keyId).child("Imagenes").child(nombreImg).updateChildren(hashMap)

                    }
                    progressDialog.dismiss( )
                    Toast.makeText(this, "Anuncio P ublicado", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

}