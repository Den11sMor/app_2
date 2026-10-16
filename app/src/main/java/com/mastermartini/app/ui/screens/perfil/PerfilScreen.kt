package com.mastermartini.app.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

// Pantalla de perfil
@Composable
fun PerfilScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Espacio reservado para la futura foto de perfil
        Card(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Foto",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Información personal",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Usuario Master Martini",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "usuario@mastermartini.cl",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Teléfono",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "+56 9 1234 5678",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tipo de negocio",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Restaurante",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tamaño del negocio",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Mediano",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar perfil")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambiar foto")
        }
    }
}