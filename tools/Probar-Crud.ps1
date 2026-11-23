param(
    [string]$BaseUrl='http://127.0.0.1:8080',
    [string]$Token=$env:MM_TOKEN
)
$ErrorActionPreference='Stop'
$headers=@{}
if($Token){$headers['Authorization']='Bearer '+$Token}
$resultados=@()
foreach($modulo in @('cursos','recetas','videos')) {
    $ruta=$BaseUrl.TrimEnd('/')+'/api/'+$modulo
    $datos=@{titulo='Prueba CRUD '+[Guid]::NewGuid().ToString('N');categoria='Pastelería';duracion='30 minutos';descripcion='Registro temporal de pruebas';detalle='Ingredientes y pasos de prueba';videoUrl='https://example.com/video.mp4'}
    $creado=$null
    try {
        $lista=Invoke-RestMethod -Uri $ruta -Headers $headers
        $creado=Invoke-RestMethod -Uri $ruta -Method Post -Headers $headers -ContentType 'application/json; charset=utf-8' -Body ($datos|ConvertTo-Json)
        if(!$creado.id){throw 'POST no entregó el identificador'}
        $consultado=Invoke-RestMethod -Uri "$ruta/$($creado.id)" -Headers $headers
        if($consultado.titulo -ne $datos.titulo){throw 'GET no devolvió el registro creado'}
        $datos.titulo+=' editado'
        $editado=Invoke-RestMethod -Uri "$ruta/$($creado.id)" -Method Put -Headers $headers -ContentType 'application/json; charset=utf-8' -Body ($datos|ConvertTo-Json)
        if($editado.titulo -ne $datos.titulo){throw 'PUT no actualizó el título'}
        Invoke-RestMethod -Uri "$ruta/$($creado.id)" -Method Delete -Headers $headers | Out-Null
        $borrado=$false
        try {Invoke-RestMethod -Uri "$ruta/$($creado.id)" -Headers $headers | Out-Null}
        catch {if([int]$_.Exception.Response.StatusCode -eq 404){$borrado=$true}else{throw}}
        if(!$borrado){throw 'DELETE no eliminó el registro'}
        $resultados += [pscustomobject]@{modulo=$modulo;listar='OK';consultar='OK';crear='OK';actualizar='OK';eliminar='OK'}
        $creado=$null
    } finally {
        # Limpia exclusivamente el registro temporal creado por esta ejecución.
        if($creado){try {Invoke-RestMethod -Uri "$ruta/$($creado.id)" -Method Delete -Headers $headers | Out-Null}catch{Write-Warning 'No se pudo limpiar el registro temporal'}}
    }
}
$resultados | Format-Table
