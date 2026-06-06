package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.DeliveryRepository
import com.example.ui.DeliveryScreen
import com.example.ui.DeliveryViewModel
import com.example.ui.DeliveryViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Room Database e Repository de forma robusta
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = DeliveryRepository(database.deliveryDao())
        val factory = DeliveryViewModelFactory(repository)
        
        // Obter ViewModel que persiste durantes mudanças de configuração (ex: rotação de tela)
        val viewModel = ViewModelProvider(this, factory)[DeliveryViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeliveryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
