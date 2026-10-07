package com.mastermartini.app.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
            .padding(24.dp)
    ) {

        Text(
            text = "Bienvenido a Master Martini",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selecciona el contenido que quieres revisar."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToRecetas,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Recetas")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToVideos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Videos")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToCursos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cursos")
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onNavigateToFavoritos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis favoritos")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onNavigateToPerfil,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mi perfil")
        }
    }
}