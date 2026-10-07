package com.mastermartini.app.ui.screens.recetas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pantalla de recetas
@Composable
fun RecetasScreen(
    onNavigateToDetalle: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Recetas",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Torta de chocolate",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Categoría: Pastelería")
                Text("Tiempo: 45 minutos")

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onNavigateToDetalle
                ) {
                    Text("Ver receta")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Brownie",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Categoría: Chocolatería")
                Text("Tiempo: 35 minutos")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Croissant",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Categoría: Bollería")
                Text("Tiempo: 60 minutos")
            }
        }
    }
}