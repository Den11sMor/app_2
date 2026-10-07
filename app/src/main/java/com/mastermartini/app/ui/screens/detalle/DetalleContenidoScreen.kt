package com.mastermartini.app.ui.screens.detalle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Detalle de una receta
@Composable
fun DetalleContenidoScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Torta de chocolate",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingredientes",
            style = MaterialTheme.typography.titleLarge
        )

        Text("250 g de harina")
        Text("200 g de chocolate")
        Text("150 g de azúcar")
        Text("3 huevos")

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Preparación",
            style = MaterialTheme.typography.titleLarge
        )

        Text("1. Mezclar los ingredientes.")
        Text("2. Agregar el chocolate.")
        Text("3. Llevar al horno.")

        Spacer(modifier = Modifier.height(20.dp))

        // Temporizador solo visual por ahora
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Temporizador",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "15:00",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar temporizador")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar a favoritos")
        }
    }
}