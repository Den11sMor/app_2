package com.mastermartini.app.ui.screens.intereses

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastermartini.app.ui.components.*

@Composable
fun InteresesScreen(onContinue: () -> Unit, volver: (() -> Unit)? = null,
                    vm: InteresesViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.guardado) { if (estado.guardado) onContinue() }
    AppPage("Tus intereses", volver) {
        Text("Selecciona los contenidos que más te interesan")
        Text("Puedes cambiarlos después desde tu perfil.")
        vm.opciones.forEach { opcion ->
            val seleccionado = opcion in estado.seleccionados
            val color by animateColorAsState(
                if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "seleccion"
            )
            FilterChip(
                selected = seleccionado, onClick = { vm.seleccionar(opcion) },
                enabled = !estado.cargando && estado.listo,
                label = { Text(opcion) },
                leadingIcon = if (seleccionado) { { Icon(Icons.Default.Check, null) } } else null,
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = color)
            )
        }
        MensajeError(estado.mensaje)
        AppButton("Guardar y continuar", estado.cargando, estado.listo, vm::guardar)
    }
}
