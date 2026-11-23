package com.mastermartini.app

import com.mastermartini.app.data.remote.*
import com.mastermartini.app.data.repository.*
import com.mastermartini.app.ui.viewmodels.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class ModuloViewModelTest(private val tipo: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun tipos() = listOf(arrayOf("Curso"), arrayOf("Receta"), arrayOf("Video"))
    }

    @Before
    fun preparar() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun terminar() {
        Dispatchers.resetMain()
    }

    private fun vm(repo: CatalogoFalso): ModuloViewModel =
        when (tipo) {
            "Curso" -> CursosViewModel(repo)
            "Video" -> VideosViewModel(repo)
            else -> RecetasViewModel(repo)
        }

    private fun llenar(vm: ModuloViewModel) {
        mapOf(
                "titulo" to "Torta",
                "categoria" to "Pastelería",
                "duracion" to "30 minutos",
                "descripcion" to "Prueba completa",
                "detalle" to "Ingredientes y pasos",
                "videoUrl" to "https://example.com/video.mp4",
            )
            .forEach { (campo, valor) -> vm.cambiar(campo, valor) }
    }

    @Test
    fun crearConsultarActualizarEliminar() = runTest {
        val repo = CatalogoFalso()
        val vm = vm(repo)
        advanceUntilIdle()
        assertFalse(vm.estado.value.ocupado)
        vm.nuevo()
        llenar(vm)
        vm.guardar()
        vm.guardar()
        advanceUntilIdle()
        assertEquals(1, repo.creaciones)
        assertNull(vm.estado.value.formulario)
        assertEquals(1, vm.estado.value.lista.size)
        val id = vm.estado.value.seleccionado!!.id
        vm.cerrar()
        vm.consultar(id)
        advanceUntilIdle()
        assertEquals("Torta", vm.estado.value.seleccionado!!.titulo)
        vm.editar()
        vm.cambiar("titulo", "Torta editada")
        vm.guardar()
        advanceUntilIdle()
        assertEquals("Torta editada", repo.filas[id]!!.titulo)
        vm.eliminar()
        advanceUntilIdle()
        assertTrue(vm.estado.value.lista.isEmpty())
        assertNull(vm.estado.value.seleccionado)
        assertTrue(repo.filas.isEmpty())
    }

    @Test
    fun validaCamposAntesDeEnviarYLimpiaErrorAlCorregir() = runTest {
        val repo = CatalogoFalso()
        val vm = vm(repo)
        advanceUntilIdle()
        vm.nuevo()
        vm.guardar()
        advanceUntilIdle()
        assertEquals(0, repo.creaciones)
        assertTrue(vm.estado.value.errores.containsKey("titulo"))
        vm.cambiar("titulo", "Corregido")
        assertFalse(vm.estado.value.errores.containsKey("titulo"))
        vm.cerrar()
        assertNull(vm.estado.value.formulario)
    }

    @Test
    fun errorServidorConservaFormularioYPermiteReintentar() = runTest {
        val repo = CatalogoFalso()
        val vm = vm(repo)
        advanceUntilIdle()
        vm.nuevo()
        llenar(vm)
        repo.error = ErrorRemoto("Revisa campos", mapOf("titulo" to "No permitido"))
        vm.guardar()
        advanceUntilIdle()
        assertEquals("Revisa campos", vm.estado.value.mensaje)
        assertEquals("No permitido", vm.estado.value.errores["titulo"])
        assertNotNull(vm.estado.value.formulario)
        assertFalse(vm.estado.value.ocupado)
        repo.error = null
        vm.cambiar("titulo", "Otro título")
        vm.guardar()
        advanceUntilIdle()
        assertNotNull(vm.estado.value.exito)
    }

    @Test
    fun falloDeConsultaYEliminacionNoOcultaDatos() = runTest {
        val repo = CatalogoFalso()
        val vm = vm(repo)
        advanceUntilIdle()
        vm.nuevo()
        llenar(vm)
        vm.guardar()
        advanceUntilIdle()
        repo.error = ErrorRemoto("Sin conexión")
        vm.eliminar()
        advanceUntilIdle()
        assertNotNull(vm.estado.value.seleccionado)
        assertEquals(1, vm.estado.value.lista.size)
        vm.cerrar()
        vm.consultar(123)
        advanceUntilIdle()
        assertNotNull(vm.estado.value.mensaje)
        assertNull(vm.estado.value.seleccionado)
    }

    @Test
    fun falloInesperadoNoDejaCargandoYNoPermiteCrearMientrasCarga() = runTest {
        val repo = CatalogoFalso()
        repo.error = IllegalStateException("Fallo")
        val vm = vm(repo)
        vm.nuevo()
        assertNull(vm.estado.value.formulario)
        advanceUntilIdle()
        assertFalse(vm.estado.value.ocupado)
        assertNotNull(vm.estado.value.mensaje)
        repo.error = null
        vm.cargar()
        advanceUntilIdle()
        assertNull(vm.estado.value.mensaje)
    }
}

class CatalogoFalso : CatalogoRemoto {
    val filas = linkedMapOf<Long, ContenidoRespuesta>()
    var error: Exception? = null
    var creaciones = 0
    private var siguiente = 1L

    private fun revisar() {
        error?.let { throw it }
    }

    override suspend fun listar(): List<ContenidoRespuesta> {
        revisar()
        return filas.values.toList()
    }

    override suspend fun consultar(id: Long): ContenidoRespuesta {
        revisar()
        return filas[id] ?: throw ErrorRemoto("No encontrado")
    }

    override suspend fun crear(datos: ContenidoSolicitud): ContenidoRespuesta {
        revisar()
        creaciones++
        return convertir(siguiente++, datos).also { filas[it.id] = it }
    }

    override suspend fun actualizar(id: Long, datos: ContenidoSolicitud): ContenidoRespuesta {
        consultar(id)
        return convertir(id, datos).also { filas[id] = it }
    }

    override suspend fun eliminar(id: Long) {
        consultar(id)
        filas.remove(id)
    }

    private fun convertir(id: Long, d: ContenidoSolicitud) =
        ContenidoRespuesta(
            id,
            d.titulo,
            d.categoria,
            d.duracion,
            d.descripcion,
            d.detalle,
            d.videoUrl,
        )
}
