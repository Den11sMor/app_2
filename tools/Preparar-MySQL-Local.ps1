param(
    [string]$MySqlBin='C:/laragon/bin/mysql/mysql-8.4.3-winx64/bin',
    [string]$DirectorioDatos=(Join-Path $PSScriptRoot '../work/mysql-local'),
    [int]$Puerto=3307
)
$ErrorActionPreference='Stop'
$configLocal=Join-Path $PSScriptRoot '../work/mysql-local/credenciales.json'
if(!$PSBoundParameters.ContainsKey('DirectorioDatos') -and (Test-Path $configLocal)) {
    $previa=Get-Content -LiteralPath $configLocal | ConvertFrom-Json
    if($previa.directorioDatos){$DirectorioDatos=$previa.directorioDatos}
}

$DirectorioDatos=[IO.Path]::GetFullPath($DirectorioDatos)
if(!(Test-Path "$MySqlBin/mysqld.exe")){throw 'Indica la carpeta bin de MySQL mediante -MySqlBin'}
New-Item -ItemType Directory -Force -Path "$DirectorioDatos/data" | Out-Null
if(!(Test-Path "$DirectorioDatos/data/auto.cnf")) {
    & "$MySqlBin/mysqld.exe" --no-defaults --initialize-insecure "--basedir=$([IO.Path]::GetDirectoryName($MySqlBin))" "--datadir=$DirectorioDatos/data" --console 2>&1 | Out-File "$DirectorioDatos/inicializacion.log"
    if($LASTEXITCODE -ne 0){throw 'Revisa inicializacion.log: no se pudo preparar MySQL'}
}
$conexion=Get-NetTCPConnection -State Listen -LocalPort $Puerto -ErrorAction SilentlyContinue
if($conexion) {
    $proceso=Get-CimInstance Win32_Process -Filter "ProcessId=$($conexion[0].OwningProcess)"
    $comandoNormalizado=$proceso.CommandLine.Replace('\','/')
    $rutaNormalizada=$DirectorioDatos.Replace('\','/')
    if($comandoNormalizado -notlike "*$rutaNormalizada*"){throw "El puerto $Puerto está ocupado por otro servidor. No se modificó ese servidor."}
} else {
    $argumentos=@('--no-defaults',"--basedir=`"$([IO.Path]::GetDirectoryName($MySqlBin))`"","--datadir=`"$DirectorioDatos/data`"","--port=$Puerto",'--bind-address=127.0.0.1','--mysqlx=OFF',"--log-error=`"$DirectorioDatos/mysql.log`"")
    Start-Process -FilePath "$MySqlBin/mysqld.exe" -ArgumentList $argumentos -WindowStyle Hidden | Out-Null
    for($intento=0;$intento -lt 40;$intento++) {
        if(Get-NetTCPConnection -State Listen -LocalPort $Puerto -ErrorAction SilentlyContinue){break}
        Start-Sleep -Milliseconds 500
    }
}
$archivo="$DirectorioDatos/credenciales.json"
if(!(Test-Path $archivo)) {
    $clave=[Guid]::NewGuid().ToString('N')+[Guid]::NewGuid().ToString('N')
    $raiz=[Guid]::NewGuid().ToString('N')+[Guid]::NewGuid().ToString('N')
    $sql="CREATE DATABASE IF NOT EXISTS master_martini CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE USER IF NOT EXISTS 'mastermartini'@'127.0.0.1' IDENTIFIED BY '$clave'; GRANT ALL PRIVILEGES ON master_martini.* TO 'mastermartini'@'127.0.0.1'; ALTER USER 'root'@'localhost' IDENTIFIED BY '$raiz';"
    $sql | & "$MySqlBin/mysql.exe" --no-defaults --host=127.0.0.1 "--port=$Puerto" --user=root
    if($LASTEXITCODE -ne 0){throw 'No se pudo configurar la base de pruebas'}
    @{usuario='mastermartini';clave=$clave;raiz=$raiz;url="jdbc:mysql://127.0.0.1:$Puerto/master_martini?sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=UTC"} | ConvertTo-Json | Set-Content -LiteralPath $archivo
}
Write-Output "MySQL listo en 127.0.0.1:$Puerto. Configuración privada: $archivo"
