package com.lvmh.pocketpet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lvmh.pocketpet.pantallas.AnalisisCategoria
import com.lvmh.pocketpet.pantallas.Calendario
import com.lvmh.pocketpet.pantallas.GraficosComparativo
import com.lvmh.pocketpet.pantallas.Metas
import com.lvmh.pocketpet.pantallas.Presupuestos
import com.lvmh.pocketpet.pantallas.Proyecciones
import com.lvmh.pocketpet.pantallas.Tendencias

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                //Tendencias()
               //Metas()
               Presupuestos()
                //AnalisisCategoria()
                //Calendario()
                //Proyecciones()}
                //GraficosComparativo()
            }
        }
    }
}
