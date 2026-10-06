package com.mastermartini.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mastermartini.app.ui.navigation.AppNavigation
import com.mastermartini.app.ui.theme.MasterMartiniTheme

// Punto de entrada de la aplicación
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MasterMartiniTheme {
                AppNavigation()
            }
        }
    }
}