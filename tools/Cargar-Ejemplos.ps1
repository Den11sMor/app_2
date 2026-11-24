param([string]$BaseUrl='http://127.0.0.1:8080',[string]$Token=$env:MM_TOKEN)
$ErrorActionPreference='Stop'
$headers=@{}
if($Token){$headers['Authorization']='Bearer '+$Token}
$datos=Get-Content -LiteralPath (Join-Path $PSScriptRoot 'demo-contenidos.json') -Raw -Encoding UTF8 | ConvertFrom-Json
foreach($modulo in @('cursos','recetas','videos')) {
    $ruta=$BaseUrl.TrimEnd('/')+'/api/'+$modulo
    $existentes=Invoke-RestMethod -Uri $ruta -Headers $headers
    if($null -ne $existentes -and $existentes.Count -gt 0){Write-Output "$modulo ya tiene registros; se conserva sin cambios.";continue}
    foreach($item in $datos.$modulo) {
        Invoke-RestMethod -Uri $ruta -Method Post -Headers $headers -ContentType 'application/json; charset=utf-8' -Body ($item|ConvertTo-Json) | Out-Null
    }
    Write-Output "Ejemplos creados en $modulo."
}
