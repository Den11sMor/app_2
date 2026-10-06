package com.mastermartini.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pantalla principal
@Composable
fun HomeScreen(
    onNavigateToRecetas: () -> Unit,
    onNavigateToVideos: () -> Unit,
    onNavigateToCursos: () -> Unit,
    onNavigateToFavoritos: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bienvenido a Master Martini",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToRecetas,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Recetas")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToVideos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Videos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToCursos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cursos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToFavoritos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Favoritos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateToPerfil,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mi perfil")
        }
    }
}