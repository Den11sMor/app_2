param(
    [string]$Configuracion=(Join-Path $PSScriptRoot '../work/mysql-local/credenciales.json'),
    [string]$BackendPath=(Join-Path $PSScriptRoot '../../backend_app'),
    [string]$JavaHome=$env:JAVA_HOME,
    [switch]$Recompilar
)
$ErrorActionPreference='Stop'
$proyecto=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$BackendPath=[IO.Path]::GetFullPath($BackendPath)
if(!(Test-Path "$BackendPath/pom.xml")){throw 'Clona backend_app junto al proyecto Android o indica -BackendPath'}
# Android usa JDK 17; el backend del equipo está configurado para Java 21 o superior.
if(!$JavaHome -or $JavaHome -like '*jdk-17*') {
    foreach($candidato in @('C:/Program Files/Java/jdk-21','C:/Program Files/Android/Android Studio/jbr')) {
        if(Test-Path "$candidato/bin/java.exe"){$JavaHome=$candidato;break}
    }
}
if(!(Test-Path "$JavaHome/bin/java.exe")){throw 'Configura JavaHome con JDK 21 o superior para el backend'}
$jar=Join-Path $BackendPath 'target/backend-0.0.1-SNAPSHOT.jar'
if($Recompilar -or !(Test-Path $jar)) {
    Push-Location $BackendPath
    try {
        $env:JAVA_HOME=$JavaHome
        & ./mvnw.cmd -B -DskipTests package
        if($LASTEXITCODE -ne 0){throw 'La compilación del backend falló'}
    } finally {Pop-Location}
}
if(Get-NetTCPConnection -State Listen -LocalPort 8080 -ErrorAction SilentlyContinue){throw 'El puerto 8080 ya está en uso. Comprueba si el backend ya está iniciado.'}
$variables=@('DB_URL','DB_USERNAME','DB_PASSWORD','JWT_SECRET')
$anteriores=@{}
foreach($nombre in $variables){$anteriores[$nombre]=[Environment]::GetEnvironmentVariable($nombre)}
try {
    if(Test-Path $Configuracion) {
        $config=Get-Content -LiteralPath $Configuracion | ConvertFrom-Json
        $env:DB_URL=if($config.urlEquipo){$config.urlEquipo}else{$config.url}
        $env:DB_USERNAME=$config.usuario;$env:DB_PASSWORD=$config.clave
        if(!$config.jwtSecret) {
            $bytes=New-Object byte[] 32
            $rng=[Security.Cryptography.RandomNumberGenerator]::Create();$rng.GetBytes($bytes);$rng.Dispose()
            $config | Add-Member -NotePropertyName jwtSecret -NotePropertyValue ([Convert]::ToBase64String($bytes)) -Force
            $config | ConvertTo-Json | Set-Content -LiteralPath $Configuracion
        }
        $env:JWT_SECRET=$config.jwtSecret
    }
    if(!$env:DB_URL -or !$env:DB_USERNAME -or !$env:JWT_SECRET){throw 'Prepara MySQL local o configura DB_URL, DB_USERNAME, DB_PASSWORD y JWT_SECRET en el entorno'}
    $salida=Join-Path $proyecto 'work/backend'
    New-Item -ItemType Directory -Force -Path $salida | Out-Null
    $copia=Join-Path $salida ('backend-'+(Get-Date -Format 'yyyyMMdd-HHmmss')+'.jar')
    Copy-Item -LiteralPath $jar -Destination $copia
    $proceso=Start-Process -FilePath "$JavaHome/bin/java.exe" -ArgumentList @('-jar',"`"$copia`"") -WindowStyle Hidden -PassThru -RedirectStandardOutput "$salida/servidor.log" -RedirectStandardError "$salida/errores.log"
    $proceso.Id | Set-Content "$salida/proceso.pid"
    Write-Output "Backend del equipo iniciado (PID $($proceso.Id)). Revisa work/backend/servidor.log."
} finally {
    foreach($nombre in $variables){[Environment]::SetEnvironmentVariable($nombre,$anteriores[$nombre])}
}
