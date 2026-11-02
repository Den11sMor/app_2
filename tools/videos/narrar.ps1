param([Parameter(Mandatory=$true)][string]$CarpetaTrabajo)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Speech
New-Item -ItemType Directory -Force -Path $CarpetaTrabajo | Out-Null
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'lessons.json') -Destination (Join-Path $CarpetaTrabajo 'lessons.json') -Force
$lecciones = Get-Content -Raw -LiteralPath (Join-Path $CarpetaTrabajo 'lessons.json') -Encoding utf8 | ConvertFrom-Json
$voz = [System.Speech.Synthesis.SpeechSynthesizer]::new()
$espanol = $voz.GetInstalledVoices() | Where-Object { $_.Enabled -and $_.VoiceInfo.Culture.Name -like 'es-*' } | Select-Object -First 1
if (-not $espanol) { throw 'Instala una voz de español en Windows para generar la narración.' }
try {
    $voz.SelectVoice($espanol.VoiceInfo.Name)
    $voz.Rate = 0
    foreach ($leccion in $lecciones.PSObject.Properties) {
        for ($i=0; $i -lt $leccion.Value.scenes.Count; $i++) {
            $voz.SetOutputToWaveFile((Join-Path $CarpetaTrabajo ($leccion.Name + '_' + $i + '.wav')))
            $voz.Speak($leccion.Value.scenes[$i].speech)
            $voz.SetOutputToNull()
        }
    }
} finally { $voz.Dispose() }