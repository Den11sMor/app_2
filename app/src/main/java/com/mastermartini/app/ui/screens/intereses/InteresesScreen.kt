package com.mastermartini.app.ui.screens.intereses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pantalla de selección de intereses
@Composable
fun InteresesScreen(
    onContinue: () -> Unit
) {
    var cocina by remember { mutableStateOf(false) }
    var recetas by remember { mutableStateOf(false) }
    var cocteleria by remember { mutableStateOf(false) }
    var gastronomia by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Elige tus intereses",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selecciona los contenidos que más te interesan"
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilterChip(
            selected = cocina,
            onClick = {
                cocina = !cocina
            },
            label = {
                Text("Cocina")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        FilterChip(
            selected = recetas,
            onClick = {
                recetas = !recetas
            },
            label = {
                Text("Recetas")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        FilterChip(
            selected = cocteleria,
            onClick = {
                cocteleria = !cocteleria
            },
            label = {
                Text("Coctelería")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        FilterChip(
            selected = gastronomia,
            onClick = {
                gastronomia = !gastronomia
            },
            label = {
                Text("Gastronomía")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}