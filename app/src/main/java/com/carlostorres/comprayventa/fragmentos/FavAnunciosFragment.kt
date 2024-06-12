package com.carlostorres.comprayventa.fragmentos

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.adapters.AnuncioAdapter
import com.carlostorres.comprayventa.databinding.FragmentFavAnunciosBinding
import com.carlostorres.comprayventa.model.AnuncioModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavAnunciosFragment : Fragment() {

    private lateinit var binding : FragmentFavAnunciosBinding
    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var anunciosArrayList : ArrayList<AnuncioModel>
    private lateinit var anunciosAdapter : AnuncioAdapter

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavAnunciosBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        cargarAnunciosFavoritos()

        binding.etBuscar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val consulta = filtro.toString()
                    anunciosAdapter.filter.filter(consulta)

                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.ibLimpiar.setOnClickListener {
            val consulta = binding.etBuscar.text.toString().trim()
            
            if (consulta.isNotEmpty()){
                binding.etBuscar.setText("")
                Toast.makeText(context, "Clear", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Nada que limpiar", Toast.LENGTH_SHORT).show()
            }
            
        }

    }

    private fun cargarAnunciosFavoritos() {

        anunciosArrayList = ArrayList()

        val refDatabase = FirebaseDatabase
            .getInstance()
            .getReference("Usuarios")

        refDatabase
            .child(firebaseAuth.uid!!)
            .child("Favoritos")
            .addValueEventListener(
                object : ValueEventListener{

                    override fun onDataChange(snapshot: DataSnapshot) {
                        anunciosArrayList.clear()
                        for (ds in snapshot.children){
                            val idAnuncio = "${ds.child("idAnuncio").value}"

                            val refFav = FirebaseDatabase.getInstance().getReference("Anuncios")
                            refFav.child(idAnuncio)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        try {
                                            val modeloAnuncio = snapshot.getValue(AnuncioModel::class.java)
                                            anunciosArrayList.add(modeloAnuncio!!)
                                        }catch (e:Exception){

                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        }

                        Handler().postDelayed({
                            anunciosAdapter = AnuncioAdapter(mContext, anunciosArrayList)
                            binding.anunciosRV.adapter = anunciosAdapter
                        }, 500)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
            )

    }

}