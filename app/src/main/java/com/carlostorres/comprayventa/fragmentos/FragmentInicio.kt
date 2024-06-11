package com.carlostorres.comprayventa.fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carlostorres.comprayventa.Constantes
import com.carlostorres.comprayventa.R
import com.carlostorres.comprayventa.RvListenerCategoria
import com.carlostorres.comprayventa.adapters.CategoriaAdapter
import com.carlostorres.comprayventa.databinding.FragmentInicioBinding
import com.carlostorres.comprayventa.model.CategoriaModel


class FragmentInicio : Fragment() {

    private lateinit var binding : FragmentInicioBinding
    private lateinit var mContext : Context

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
        cargarCategorias()
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

                }
            }
        )

        binding.catRv.adapter = adaptadorCategoria

    }

}