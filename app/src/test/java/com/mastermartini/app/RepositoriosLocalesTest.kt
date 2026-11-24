package com.mastermartini.app

import com.mastermartini.app.data.local.dao.*
import com.mastermartini.app.data.local.entity.*
import com.mastermartini.app.data.repository.*
import com.mastermartini.app.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class RepositoriosLocalesTest {
 @Test fun catalogoInicialConservaTiposYActualizaLosVideos()=runTest {
  val filas=MutableStateFlow<List<ContenidoEntity>>(emptyList());var actualizados=0
  val dao=object:ContenidoDao {
   override fun observarTodos()=filas
   override suspend fun insertarTodos(contenidos:List<ContenidoEntity>){filas.value=contenidos}
   override suspend fun actualizarVideo(id:Int,duracion:String,descripcion:String,detalle:String){actualizados++}
  }
  val lista=ContenidoRepository(dao).contenidos.first();assertEquals(7,lista.size);assertEquals(2,actualizados)
  assertEquals("Ver receta",lista.first{it is Receta}.accion());assertEquals("Ver curso",lista.first{it is Curso}.accion());assertEquals("Ver video",lista.first{it is Video}.accion())
  assertNotNull(lista.first{it.id==4}.obtenerVideoResId());assertNotNull(lista.first{it.id==5}.obtenerVideoResId());assertNull(lista.first().obtenerVideoResId())
 }
 @Test fun favoritosAgregaQuitaYAislaCadaUsuario()=runTest {
  val filas=mutableSetOf<Pair<Long,Int>>()
  val dao=object:FavoritoDao {
   override fun observarIds(usuarioId:Long)=flowOf(filas.filter{it.first==usuarioId}.map{it.second})
   override suspend fun agregar(favorito:FavoritoEntity){filas.add(favorito.usuarioId to favorito.contenidoId)}
   override suspend fun quitar(usuarioId:Long,contenidoId:Int){filas.remove(usuarioId to contenidoId)}
  }
  val repo=FavoritoRepository(dao);repo.cambiar(1,4,false);repo.cambiar(2,5,false)
  assertEquals(listOf(4),repo.observarIds(1).first());repo.cambiar(1,4,true);assertTrue(repo.observarIds(1).first().isEmpty());assertEquals(listOf(5),repo.observarIds(2).first())
  try{repo.cambiar(0,4,false);fail("Requiere sesión")}catch(_:IllegalStateException){}
 }
}
