package com.mastermartini.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.mastermartini.app.ui.screens.login.LoginScreen
import com.mastermartini.app.ui.screens.registro.RegistroScreen
import com.mastermartini.app.ui.screens.intereses.InteresesScreen
import com.mastermartini.app.ui.screens.home.*
import com.mastermartini.app.ui.screens.recetas.RecetasScreen
import com.mastermartini.app.ui.screens.detalle.DetalleContenidoScreen
import com.mastermartini.app.ui.screens.videos.VideosScreen
import com.mastermartini.app.ui.screens.cursos.CursosScreen
import com.mastermartini.app.ui.screens.favoritos.FavoritosScreen
import com.mastermartini.app.ui.screens.perfil.PerfilScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INTERESES = "intereses"
    const val EDITAR_INTERESES = "editar_intereses"
    const val HOME = "home"
    const val RECETAS = "recetas"
    const val DETALLE = "detalle/{id}"
    const val VIDEOS = "videos"
    const val CURSOS = "cursos"
    const val FAVORITOS = "favoritos"
    const val PERFIL = "perfil"
}

@Composable
fun AppNavigation(vm: HomeViewModel = viewModel()) {
    val nav = rememberNavController()
    val inicio = remember { if (vm.idInicial == 0L) Routes.LOGIN else Routes.HOME }
    val volver: () -> Unit = { nav.popBackStack(); Unit }
    val abrir: (Int) -> Unit = { id -> nav.navigate("detalle/$id") }
    fun irAlInicio() {
        nav.navigate(Routes.HOME) {
            popUpTo(nav.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    NavHost(
        navController = nav, startDestination = inicio,
        enterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(200)) { it / 12 } },
        exitTransition = { fadeOut(tween(150)) },
        popEnterTransition = { fadeIn(tween(200)) },
        popExitTransition = { fadeOut(tween(150)) }
    ) {
        composable(Routes.LOGIN) {
            LoginScreen({ nav.navigate(Routes.REGISTRO) }, { irAlInicio() })
        }
        composable(Routes.REGISTRO) {
            RegistroScreen(onRegistroSuccess = {
                nav.navigate(Routes.INTERESES) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }, onNavigateToLogin = volver)
        }
        composable(Routes.INTERESES) { InteresesScreen(onContinue = { irAlInicio() }) }
        composable(Routes.EDITAR_INTERESES) { InteresesScreen(onContinue = volver, volver = volver) }
        composable(Routes.HOME) {
            HomeScreen(
                { nav.navigate(Routes.RECETAS) }, { nav.navigate(Routes.VIDEOS) },
                { nav.navigate(Routes.CURSOS) }, { nav.navigate(Routes.FAVORITOS) },
                { nav.navigate(Routes.PERFIL) }
            )
        }
        composable(Routes.RECETAS) { RecetasScreen(volver, abrir) }
        composable(Routes.VIDEOS) { VideosScreen(volver, abrir) }
        composable(Routes.CURSOS) { CursosScreen(volver, abrir) }
        composable(Routes.FAVORITOS) {
            FavoritosScreen(
                volver = volver,
                abrir = abrir,
                onNavigateToRecetas = { nav.navigate(Routes.RECETAS) },
                onNavigateToVideos = { nav.navigate(Routes.VIDEOS) },
                onNavigateToCursos = { nav.navigate(Routes.CURSOS) }
            )
        }
        composable(Routes.DETALLE, arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            DetalleContenidoScreen(it.arguments?.getInt("id") ?: 0, volver)
        }
        composable(Routes.PERFIL) {
            PerfilScreen(volver, { nav.navigate(Routes.EDITAR_INTERESES) }, {
                vm.cerrarSesion()
                nav.navigate(Routes.LOGIN) {
                    popUpTo(nav.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
    }
}
