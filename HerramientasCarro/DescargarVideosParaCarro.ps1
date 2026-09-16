# Descargador de videos para USB del carro (Ford SYNC reproduce MP4 detenido).
# Sin admin: todo en tu carpeta de usuario.
# Uso: clic derecho > Ejecutar con PowerShell. Edita $Videos para agregar enlaces.

$ToolsDir = "$env:USERPROFILE\ToolsCarro"
$YtDlp = "$ToolsDir\yt-dlp.exe"
$YtDlpUrl = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe"
$OutDir = "$env:USERPROFILE\VideosCarro"

$Videos = @(
    "https://www.youtube.com/watch?v=M7FIvfx5J10"  # Epic Split (verificado OK)
    # Agrega aqui tus enlaces (los en vivo no se pueden descargar):
    # "https://www.youtube.com/watch?v=VIDEO_ID",
    # (DWcJFNfaw9c no disponible)
    # (tGBRkQvf8B8 no disponible)
    # (jfKfPfyJRdk es en vivo: no descargable)
    # (5qap5aO4i9A es en vivo: no descargable)
)

New-Item -ItemType Directory -Path $ToolsDir -Force | Out-Null
New-Item -ItemType Directory -Path $OutDir -Force | Out-Null

if (-not (Test-Path $YtDlp)) {
    Write-Host "Descargando yt-dlp (solo primera vez)..."
    $ProgressPreference = "SilentlyContinue"
    Invoke-WebRequest -Uri $YtDlpUrl -OutFile $YtDlp
}
Write-Host "yt-dlp listo."
& $YtDlp --version

$ok = 0
$fail = 0
foreach ($url in $Videos) {
    Write-Host ""
    Write-Host "=== $url ==="
    try {
        & $YtDlp --no-check-certificate -f "bv*[ext=mp4][height<=720]+ba[ext=m4a]/b[ext=mp4]/b" --merge-output-format mp4 -o "$OutDir/%(title)s.%(ext)s" --no-playlist "$url"
        if ($LASTEXITCODE -eq 0) { $ok++ } else { $fail++; Write-Host "Omitido (en vivo, privado o no disponible)." }
    } catch {
        $fail++
        Write-Host "Omitido por error."
    }
}

Write-Host ""
Write-Host "Listo: $ok descargados, $fail omitidos en $OutDir"
Write-Host ""
Write-Host "USB detectadas (extraibles):"
Get-PSDrive -PSProvider FileSystem | Where-Object { $_.Description -like "*extra*" -or $_.Description -like "*emov*" -or $_.Description -like "*USB*" } | Format-Table Name, Description, Free
Write-Host "Copia la carpeta VideosCarro a la USB (FAT32 o exFAT). En el carro: Fuentes > USB, con el carro detenido (P)."
Read-Host "Enter para salir" | Out-Null
