package com.mastermartini.app

import com.mastermartini.app.data.repository.*
import com.mastermartini.app.domain.model.*
import com.mastermartini.app.ui.viewmodels.*
import com.mastermartini.app.utils.Validaciones
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class LogicaMovilTest {
 private lateinit var app:MasterMartiniApplication
 private lateinit var usuarios:UsuarioRepository
 private lateinit var contenidos:ContenidoRepository
 private lateinit var favoritos:FavoritoRepository
 private lateinit var usuario:MutableStateFlow<Usuario?>
 private val persona=Usuario(7,"Luis Prueba","luis@test.cl","912345678",listOf("Cocina"),"")
 @Before fun preparar() {
  Dispatchers.setMain(StandardTestDispatcher())
  app=mock(MasterMartiniApplication::class.java);usuarios=mock(UsuarioRepository::class.java)
  contenidos=mock(ContenidoRepository::class.java);favoritos=mock(FavoritoRepository::class.java)
  usuario=MutableStateFlow(persona)
  `when`(app.usuarios).thenReturn(usuarios);`when`(app.contenidos).thenReturn(contenidos);`when`(app.favoritos).thenReturn(favoritos)
  `when`(usuarios.usuario).thenReturn(usuario);`when`(usuarios.idActual).thenReturn(7)
  `when`(contenidos.contenidos).thenReturn(flowOf(listOf(Receta(1,"Torta","Pastelería","30 min","Descripción","Pasos"))))
  `when`(favoritos.observarIds(7)).thenReturn(flowOf(listOf(1)))
 }
 @After fun terminar(){Dispatchers.resetMain()}
 @Test fun loginValidaCredencialesYRespondeAlResultadoDelRepositorio()=runTest {
  val vm=LoginViewModel(app);vm.ingresar();assertNotNull(vm.estado.value.errorCorreo)
  vm.correo("luis@test.cl");vm.clave("Martini2026")
  `when`(usuarios.ingresar("luis@test.cl","Martini2026")).thenReturn(false)
  vm.ingresar();advanceUntilIdle();assertFalse(vm.estado.value.exitoso);assertNotNull(vm.estado.value.mensaje)
  `when`(usuarios.ingresar("luis@test.cl","Martini2026")).thenReturn(true)
  vm.ingresar();advanceUntilIdle();assertTrue(vm.estado.value.exitoso);assertEquals("",vm.estado.value.clave)
 }
 @Test fun loginMuestraErroresSinQuedarCargando()=runTest {
  val vm=LoginViewModel(app);vm.correo("luis@test.cl");vm.clave("Martini2026")
  doAnswer { throw ErrorRemoto("Servidor apagado") }.`when`(usuarios).ingresar("luis@test.cl","Martini2026")
  vm.ingresar();advanceUntilIdle();assertEquals("Servidor apagado",vm.estado.value.mensaje)
  doThrow(IllegalStateException()).`when`(usuarios).ingresar("luis@test.cl","Martini2026")
  vm.ingresar();advanceUntilIdle();assertFalse(vm.estado.value.cargando);assertNotNull(vm.estado.value.mensaje)
 }
 private fun llenar(vm:RegistroViewModel){mapOf("nombre" to "Luis Prueba","correo" to "luis@test.cl","telefono" to "912345678","clave" to "Martini2026","repetida" to "Martini2026").forEach{(c,v)->vm.cambiar(c,v)}}
 @Test fun registroValidaTodosLosCamposYConfirmaGuardado()=runTest {
  val vm=RegistroViewModel(app);vm.registrar();assertTrue(vm.estado.value.errores.values.any{it!=null})
  llenar(vm);vm.cambiar("campo-desconocido","");vm.registrar();advanceUntilIdle()
  assertTrue(vm.estado.value.exitoso);assertEquals("",vm.estado.value.clave);assertEquals("",vm.estado.value.repetida)
 }
 @Test fun registroDistingueDuplicadoErrorDeRedYErrorInesperado()=runTest {
  val vm=RegistroViewModel(app);llenar(vm)
  `when`(usuarios.registrar("Luis Prueba","luis@test.cl","912345678","Martini2026")).thenReturn("Duplicado")
  vm.registrar();advanceUntilIdle();assertEquals("Duplicado",vm.estado.value.mensaje);assertFalse(vm.estado.value.exitoso)
  doAnswer { throw ErrorRemoto("Sin red") }.`when`(usuarios).registrar("Luis Prueba","luis@test.cl","912345678","Martini2026")
  vm.registrar();advanceUntilIdle();assertEquals("Sin red",vm.estado.value.mensaje)
  doThrow(IllegalStateException()).`when`(usuarios).registrar("Luis Prueba","luis@test.cl","912345678","Martini2026")
  vm.registrar();advanceUntilIdle();assertFalse(vm.estado.value.cargando);assertNotNull(vm.estado.value.mensaje)
 }
 @Test fun interesesCarganSeleccionanValidanYSeGuardan()=runTest {
  val vm=InteresesViewModel(app);vm.guardar();advanceUntilIdle();assertTrue(vm.estado.value.listo)
  vm.seleccionar("Cocina");vm.guardar();assertNotNull(vm.estado.value.mensaje)
  vm.seleccionar("Recetas");vm.guardar();advanceUntilIdle();assertTrue(vm.estado.value.guardado)
  verify(usuarios).guardarIntereses(setOf("Recetas"));assertEquals(4,vm.opciones.size)
 }
 @Test fun interesesMuestranErrorSiLaPersistenciaFalla()=runTest {
  val vm=InteresesViewModel(app);advanceUntilIdle()
  doAnswer{throw IllegalStateException()}.`when`(usuarios).guardarIntereses(setOf("Cocina"))
  vm.guardar();advanceUntilIdle();assertNotNull(vm.estado.value.mensaje);assertFalse(vm.estado.value.cargando)
 }
 @Test fun perfilCargaValidaEditaYConservaCambiosMientrasLleganDatos()=runTest {
  val vm=PerfilViewModel(app);vm.guardar();advanceUntilIdle();assertEquals(persona.correo,vm.estado.value.correo)
  vm.nombre(" ");vm.telefono("abc");vm.guardar();assertNotNull(vm.estado.value.errorNombre);assertNotNull(vm.estado.value.errorTelefono)
  vm.nombre("Luis Actualizado");vm.telefono("923456789");vm.guardar();advanceUntilIdle();assertEquals("Perfil actualizado",vm.estado.value.aviso)
  usuario.value=persona.copy(foto="foto.jpg");advanceUntilIdle();assertEquals("Luis Actualizado",vm.estado.value.nombre);assertEquals("foto.jpg",vm.estado.value.foto)
  vm.errorFoto();assertNotNull(vm.estado.value.mensaje)
 }
 @Test fun perfilMuestraErrorAlFallarGuardado()=runTest {
  val vm=PerfilViewModel(app);advanceUntilIdle()
  doAnswer{throw IllegalStateException()}.`when`(usuarios).guardarPerfil("Luis Prueba","912345678")
  vm.guardar();advanceUntilIdle();assertNotNull(vm.estado.value.mensaje);assertFalse(vm.estado.value.cargando)
 }
 @Test fun catalogoCombinaFavoritosBusquedaYActualizacion()=runTest {
  val vm=FavoritosViewModel(app);advanceUntilIdle();assertEquals(1,vm.estado.value.contenidos.size);assertEquals(listOf(1),vm.estado.value.favoritos)
  vm.buscar("Torta");assertEquals("Torta",vm.consulta.value)
  vm.cambiarFavorito(1);advanceUntilIdle();verify(favoritos).cambiar(7,1,true);assertFalse(vm.estado.value.guardando)
  vm.cargar();advanceUntilIdle();assertFalse(vm.estado.value.cargando)
 }
 @Test fun catalogoComunicaFallosYRecuperaElEstado()=runTest {
  `when`(contenidos.contenidos).thenReturn(flow{throw IllegalStateException()})
  val vm=ContenidoViewModel(app);advanceUntilIdle();assertNotNull(vm.estado.value.mensaje)
  `when`(contenidos.contenidos).thenReturn(flowOf(emptyList()));vm.cargar();advanceUntilIdle();assertNull(vm.estado.value.mensaje)
  doAnswer{throw IllegalStateException()}.`when`(favoritos).cambiar(7,8,false)
  vm.cambiarFavorito(8);advanceUntilIdle();assertNotNull(vm.estado.value.mensaje);assertFalse(vm.estado.value.guardando)
  usuario.value=null;advanceUntilIdle();assertTrue(vm.estado.value.favoritos.isEmpty())
 }
 @Test fun inicioComparteSesionYPermiteCerrarla()=runTest {
  val vm=HomeViewModel(app);assertEquals(7L,vm.idInicial)
  assertEquals(persona,vm.usuario.first{it!=null});vm.cerrarSesion();verify(usuarios).cerrarSesion()
 }
 @Test fun reglasDeNombreCubrenFormatosInvalidosYValidos(){
  listOf(" ","Ab","A".repeat(61),"Luis 123","Luis").forEach{assertNotNull(Validaciones.nombre(it))}
  assertNull(Validaciones.nombre("Luis Prueba"))
 }
}
