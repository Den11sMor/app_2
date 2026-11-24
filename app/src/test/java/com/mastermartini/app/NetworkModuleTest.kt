package com.mastermartini.app

import android.content.Context
import android.content.SharedPreferences
import com.mastermartini.app.data.remote.NetworkModule
import com.mastermartini.app.data.remote.dto.LoginRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*

class NetworkModuleTest {
 @Test fun configuracionCompartidaEnviaJwtYLoBorraAlCambiarDeServidor()=runTest {
  val valores=mutableMapOf<String,Any>();val contexto=mock(Context::class.java)
  val prefs=mock(SharedPreferences::class.java);val editor=mock(SharedPreferences.Editor::class.java)
  `when`(contexto.getSharedPreferences("servidor",Context.MODE_PRIVATE)).thenReturn(prefs)
  `when`(prefs.edit()).thenReturn(editor)
  `when`(prefs.getString(anyString(),anyString())).thenAnswer { valores[it.getArgument<String>(0)] as? String ?: it.getArgument<String>(1) }
  `when`(prefs.getBoolean(anyString(),anyBoolean())).thenAnswer { valores[it.getArgument<String>(0)] as? Boolean ?: it.getArgument<Boolean>(1) }
  `when`(editor.putString(anyString(),anyString())).thenAnswer {valores[it.getArgument<String>(0)]=it.getArgument<String>(1);editor}
  `when`(editor.putBoolean(anyString(),anyBoolean())).thenAnswer {valores[it.getArgument<String>(0)]=it.getArgument<Boolean>(1);editor}
  `when`(editor.remove(anyString())).thenAnswer {valores.remove(it.getArgument<String>(0));editor}
  val red=NetworkModule(contexto);assertTrue(red.usuariosRemotos);assertEquals("http://127.0.0.1:8080/",red.url)
  val servidor=MockWebServer();servidor.start()
  try {
   red.guardar(servidor.url("/").newBuilder().host("127.0.0.1").build().toString());red.guardarToken("token-anterior")
   servidor.enqueue(MockResponse().setBody("[]"));assertTrue(red.repositorio("Curso").listar().isEmpty())
   assertEquals("Bearer token-anterior",servidor.takeRequest().getHeader("Authorization"))
   servidor.enqueue(MockResponse().setBody("""{"token":"token-nuevo","tipo":"Bearer","usuario":{"idUsuario":1,"nombre":"Luis Prueba","correo":"luis@test.cl"}}"""))
   assertNotNull(red.usuarios().ingresar(LoginRequest("luis@test.cl","Martini2026")))
   val login=servidor.takeRequest();assertEquals("/api/usuarios/login",login.path);assertNull(login.getHeader("Authorization"))
   servidor.enqueue(MockResponse().setBody("[]"));red.repositorio("Receta").listar()
   assertEquals("Bearer token-nuevo",servidor.takeRequest().getHeader("Authorization"))
   assertNotNull(red.repositorio("Video"));assertNotNull(red.recetasExternas())
   red.guardar("https://example.com/",false);assertFalse(red.usuariosRemotos);assertFalse(valores.containsKey("token"))
   red.guardarToken("temporal");red.cerrarSesion();assertFalse(valores.containsKey("token"))
  } finally {servidor.shutdown()}
 }
}
