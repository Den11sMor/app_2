param([string]$BaseUrl='http://127.0.0.1:8080')
$ErrorActionPreference='Stop'
$directorio=Join-Path $PSScriptRoot '../work/pruebas'
New-Item -ItemType Directory -Force -Path $directorio | Out-Null
$archivo=Join-Path $directorio 'cuenta-demo.json'
if(Test-Path $archivo) {
    $cuenta=Get-Content -LiteralPath $archivo | ConvertFrom-Json
} else {
    $cuenta=[pscustomobject]@{correo=('prueba.'+[DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()+'@mastermartini.test');password=('Demo2026'+[Guid]::NewGuid().ToString('N'))}
    $registro=@{nombre='Usuario Prueba';correo=$cuenta.correo;telefono='912345678';password=$cuenta.password;intereses=@('Cocina')}
    Invoke-RestMethod -Uri ($BaseUrl.TrimEnd('/')+'/api/usuarios/registro') -Method Post -ContentType 'application/json; charset=utf-8' -Body ($registro|ConvertTo-Json) | Out-Null
    $cuenta | ConvertTo-Json | Set-Content -LiteralPath $archivo
}
$respuesta=Invoke-RestMethod -Uri ($BaseUrl.TrimEnd('/')+'/api/usuarios/login') -Method Post -ContentType 'application/json; charset=utf-8' -Body ($cuenta|ConvertTo-Json)
if(!$respuesta.token){throw 'El servidor no entregó un JWT'}
& (Join-Path $PSScriptRoot 'Cargar-Ejemplos.ps1') -BaseUrl $BaseUrl -Token $respuesta.token
& (Join-Path $PSScriptRoot 'Probar-Crud.ps1') -BaseUrl $BaseUrl -Token $respuesta.token
Write-Output "Registro, login JWT y CRUD comprobados. Cuenta de prueba guardada en $archivo (no subir a Git)."
