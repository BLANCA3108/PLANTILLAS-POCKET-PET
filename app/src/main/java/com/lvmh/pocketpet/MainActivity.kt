package com.lvmh.pocketpet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lvmh.pocketpet.presentacion.navegacion.NavigationHost
import com.lvmh.pocketpet.presentacion.tema.PocketPetTema
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel

class MainActivity : ComponentActivity() {

    private val estadisticasViewModel: EstadisticasViewModel by viewModels()
    private val presupuestoViewModel: PresupuestoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PocketPetTema {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationHost(
                        estadisticasViewModel = estadisticasViewModel,
                        presupuestoViewModel = presupuestoViewModel,
                        usuarioId = "usuario_demo_123"
                    )
                }
            }
        }
    }
}