package com.carlostorres.comprayventa.fragmentos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.RvListenerCategoria
import com.carlostorres.comprayventa.SeleccionarUbicacionActivity
import com.carlostorres.comprayventa.adapters.AnuncioAdapter
import com.carlostorres.comprayventa.adapters.CategoriaAdapter
import com.carlostorres.comprayventa.databinding.FragmentInicioBinding
import com.carlostorres.comprayventa.model.AnuncioModel
import com.carlostorres.comprayventa.model.CategoriaModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentInicio : Fragment() {

    private lateinit var binding : FragmentInicioBinding

    private companion object{
        private const val MAX_DISTANCIA_MOSTRAR_ANUNCIO = 10
    }

    private lateinit var mContext : Context

    private lateinit var anuncioArrayList: ArrayList<AnuncioModel>
    private lateinit var adaptadorAnuncio : AnuncioAdapter
    private lateinit var locationSP : SharedPreferences

    private  var actualLatitud = 0.0
    private  var actualLongitud = 0.0
    private var actualDireccion = ""

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(LayoutInflater.from(mContext),container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationSP = mContext.getSharedPreferences( "LOCATION_SP", Context.MODE_PRIVATE)

        actualLatitud = locationSP.getFloat("ACTUAL_LATITUD", 0.0f).toDouble()
        actualLongitud = locationSP.getFloat("ACTUAL_LONGITUD", 0.0f).toDouble()
        actualDireccion = locationSP.getString("ACTUAL_DIRECCION","")!!

        if (actualLatitud != 0.0 && actualLongitud != 0.0){

            binding.tvLocacion.text = actualDireccion

        }

        cargarCategorias()

        cargarAnuncios("Todos")

        binding.tvLocacion.setOnClickListener {
            val intent = Intent(mContext, SeleccionarUbicacionActivity::class.java)
            seleccionarUbicacionARL.launch(intent)
        }

        binding.etBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val consulta = filtro.toString()
                    adaptadorAnuncio.filter.filter(consulta)
                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.ibClean.setOnClickListener {
            val consulta = binding.etBuscar.text.toString().trim()
            if (consulta.isNotEmpty()){
                binding.etBuscar.setText("")
                Toast.makeText(context, "Clean", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Nada que limpiar", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * Lanza la actividad para seleccionar una ubicacion en el mapa
     * @author Carlos Toral
     */
    //region seleccionarUbicacionARL
    private val seleccionarUbicacionARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            val data = it.data
            if (data != null){
                actualLatitud = data.getDoubleExtra("latitud", 0.0)
                actualLongitud = data.getDoubleExtra("longitud", 0.0)
                actualDireccion = data.getStringExtra("direccion").toString()

                locationSP.edit()
                    .putFloat("ACTUAL_LATITUD", actualLatitud.toFloat())
                    .putFloat("ACTUAL_LONGITUD", actualLongitud.toFloat())
                    .putString("ACTUAL_DIRECCION", actualDireccion)
                    .apply()

                binding.tvLocacion.text = actualDireccion

                cargarAnuncios("Todos")

            }else{
                Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //endregion

    /**
     * Carga la lista de anuncios de la categoria seleccionada
     * @author Carlos Toral
     * @param categoria String que indica la categoria de anuncios a buscar
     */
    //region cargarAnuncios
    private fun cargarAnuncios(categoria: String) {

        anuncioArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")
        ref.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                anuncioArrayList.clear()

                for (ds in snapshot.children){

                    try {

                        val modelAnuncio = ds.getValue(AnuncioModel::class.java)

                        val distancia = calcularDistanciaKM(
                            modelAnuncio?.latitud ?: 0.0,
                            modelAnuncio?.longitud ?: 0.0,
                        )

                        if (categoria == "Todos"){

                            if (distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                anuncioArrayList.add(modelAnuncio!!)

                            }
                        }else{

                            if (modelAnuncio!!.categoria.equals(categoria)){

                                if (distancia <= MAX_DISTANCIA_MOSTRAR_ANUNCIO){
                                    anuncioArrayList.add(modelAnuncio)

                                }
                            }
                        }
                    }catch (e: Exception){

                    }
                }

                adaptadorAnuncio = AnuncioAdapter(mContext, anuncioArrayList)
                println()
                binding.rvAnuncios.adapter = adaptadorAnuncio

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    //endregion

    private fun calcularDistanciaKM(latitud: Double, longitud: Double): Double {

        val puntoPartida = Location(LocationManager.NETWORK_PROVIDER)
        puntoPartida.latitude = actualLatitud
        puntoPartida.longitude = actualLongitud

        val puntoFinal = Location(LocationManager.NETWORK_PROVIDER)
        puntoFinal.latitude = latitud
        puntoFinal.longitude = longitud

        val distanciMetros = puntoPartida.distanceTo(puntoFinal).toDouble()
        return distanciMetros/1000

    }

    private fun cargarCategorias(){
        val categoriasArrayList = ArrayList<CategoriaModel>()
        for (i in 0 until Constantes.categorias.size){

            val modeloCategoria = CategoriaModel(
                categoria = Constantes.categorias[i],
                ic =  Constantes.categoriasIcono[i]
            )

            categoriasArrayList.add(modeloCategoria)

        }

        val adaptadorCategoria = CategoriaAdapter(
            context = mContext,
            categoriaArrayList =  categoriasArrayList,
            rvListenerCategoria = object : RvListenerCategoria{
                override fun onCategoriaClick(categoria: CategoriaModel) {
                    val categoriaSeleccionada = categoria.categoria
                    cargarAnuncios(categoriaSeleccionada)
                }
            }
        )

        binding.catRv.adapter = adaptadorCategoria

    }

}