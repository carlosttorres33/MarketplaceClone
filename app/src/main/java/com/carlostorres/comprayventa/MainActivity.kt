package com.carlostorres.comprayventa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.carlostorres.comprayventa.databinding.ActivityMainBinding
import com.carlostorres.comprayventa.fragmentos.FragmentChats
import com.carlostorres.comprayventa.fragmentos.FragmentCuenta
import com.carlostorres.comprayventa.fragmentos.FragmentInicio
import com.carlostorres.comprayventa.fragmentos.FragmentMisAnuncios

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verFragmentInicio()

        binding.bottomNv.setOnItemSelectedListener { item ->

            when(item.itemId){

                R.id.item_inicio -> {
                    verFragmentInicio()
                    true
                }
                R.id.item_chats -> {
                    verFragmentChats()
                    true
                }
                R.id.item_mis_anuncios -> {
                    verFragmentMisAnuncios()
                    true
                }
                R.id.item_icuenta -> {
                    verFragmentCuenta()
                    true
                }
                else -> false

            }

        }

    }

    private fun verFragmentInicio(){

        binding.tituloRl.text = "Inicio"

        val fragment = FragmentInicio()
        val fragmentTransition = supportFragmentManager.beginTransaction()

        fragmentTransition.replace(binding.fragmentLayout1.id, fragment, "FragmentInicio")
        fragmentTransition.commit()

    }

    private fun verFragmentChats(){

        binding.tituloRl.text = "Chats"

        val fragment = FragmentChats()
        val fragmentTransition = supportFragmentManager.beginTransaction()

        fragmentTransition.replace(binding.fragmentLayout1.id, fragment, "FragmentChats")
        fragmentTransition.commit()

    }

    private fun verFragmentMisAnuncios(){

        binding.tituloRl.text = "Mis Anuncios"

        val fragment = FragmentMisAnuncios()
        val fragmentTransition = supportFragmentManager.beginTransaction()

        fragmentTransition.replace(binding.fragmentLayout1.id, fragment, "FragmentMisAnuncios")
        fragmentTransition.commit()

    }

    private fun verFragmentCuenta(){

        binding.tituloRl.text = "Cuenta"

        val fragment = FragmentCuenta()
        val fragmentTransition = supportFragmentManager.beginTransaction()

        fragmentTransition.replace(binding.fragmentLayout1.id, fragment, "FragmentCuenta")
        fragmentTransition.commit()

    }

}