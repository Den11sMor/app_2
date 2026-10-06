package com.mastermartini.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.mastermartini.app.ui.screens.login.LoginScreen
import com.mastermartini.app.ui.screens.registro.RegistroScreen
import com.mastermartini.app.ui.screens.intereses.InteresesScreen
import com.mastermartini.app.ui.screens.home.HomeScreen
import com.mastermartini.app.ui.screens.recetas.RecetasScreen
import com.mastermartini.app.ui.screens.detalle.DetalleContenidoScreen
import com.mastermartini.app.ui.screens.videos.VideosScreen
import com.mastermartini.app.ui.screens.cursos.CursosScreen
import com.mastermartini.app.ui.screens.favoritos.FavoritosScreen
import com.mastermartini.app.ui.screens.perfil.PerfilScreen

// Rutas de la aplicación
object Routes {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INTERESES = "intereses"
    const val HOME = "home"
    const val RECETAS = "recetas"
    const val DETALLE = "detalle"
    const val VIDEOS = "videos"
    const val CURSOS = "cursos"
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"
}

// Controla la navegación entre pantallas
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegistro = {
                    navController.navigate(Routes.REGISTRO)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Registro
        composable(Routes.REGISTRO) {
            RegistroScreen(
                onRegistroSuccess = {
                    navController.navigate(Routes.INTERESES)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Intereses
        composable(Routes.INTERESES) {
            InteresesScreen(
                onContinue = {
                    navController.navigate(Routes.HOME)
                }
            )
        }

        // Inicio
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToRecetas = {
                    navController.navigate(Routes.RECETAS)
                },
                onNavigateToVideos = {
                    navController.navigate(Routes.VIDEOS)
                },
                onNavigateToCursos = {
                    navController.navigate(Routes.CURSOS)
                },
                onNavigateToFavoritos = {
                    navController.navigate(Routes.FAVORITOS)
                },
                onNavigateToPerfil = {
                    navController.navigate(Routes.PERFIL)
                }
            )
        }

        // Recetas
        composable(Routes.RECETAS) {
            RecetasScreen(
                onNavigateToDetalle = {
                    navController.navigate(Routes.DETALLE)
                }
            )
        }

        // Detalle
        composable(Routes.DETALLE) {
            DetalleContenidoScreen()
        }

        // Videos
        composable(Routes.VIDEOS) {
            VideosScreen()
        }

        // Cursos
        composable(Routes.CURSOS) {
            CursosScreen()
        }

        // Favoritos
        composable(Routes.FAVORITOS) {
            FavoritosScreen()
        }

        // Perfil
        composable(Routes.PERFIL) {
            PerfilScreen()
        }
    }
}