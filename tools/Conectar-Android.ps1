param(
    [string]$Sdk=$env:ANDROID_HOME,
    [string]$Apk=(Join-Path $PSScriptRoot '../app/build/outputs/apk/release/app-release.apk')
)
$ErrorActionPreference='Stop'
if(!$Sdk){$Sdk=Join-Path $env:LOCALAPPDATA 'Android/Sdk'}
$adb=Join-Path $Sdk 'platform-tools/adb.exe'
if(!(Test-Path $adb)){throw 'No se encontró adb. Indica -Sdk con la carpeta del Android SDK'}
$dispositivos=@(& $adb devices | Select-String '^\S+\s+device$')
if($dispositivos.Count -ne 1){throw 'Conecta exactamente un celular, activa Depuración USB y acepta su autorización'}
if(!(Test-Path $Apk)){throw 'Primero genera o descarga el APK firmado'}
& $adb reverse tcp:8080 tcp:8080
if($LASTEXITCODE -ne 0){throw 'No se pudo conectar el teléfono al backend local'}
& $adb install -r $Apk
if($LASTEXITCODE -ne 0){throw 'No se pudo instalar. No se desinstaló ni borró ninguna aplicación anterior.'}
& $adb shell am start -n com.mastermartini.app.integracion/com.mastermartini.app.MainActivity
Write-Output 'Abre Recetas, Videos o Cursos y pulsa Gestionar contenidos del servidor. El cable USB debe seguir conectado.'
