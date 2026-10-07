package com.mastermartini.app.ui.screens.cursos

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

// Pantalla de cursos
@Composable
fun CursosScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Cursos",
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
                    text = "Pastelería básica",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Nivel: Principiante")
                Text("Duración: 4 horas")

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { }
                ) {
                    Text("Ver curso")
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
                    text = "Introducción a la chocolatería",
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Nivel: Principiante")
                Text("Duración: 3 horas")
            }
        }
    }
}