$ErrorActionPreference = "Stop"
Set-Location -Path $PSScriptRoot

$mavenVersion = "3.9.6"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$tempDir = "$env:TEMP\maven"
$mavenZip = "$tempDir\maven.zip"
$mavenDir = "$tempDir\apache-maven-$mavenVersion"
$mvnCmd = "$mavenDir\bin\mvn.cmd"

if (-not (Test-Path $mvnCmd)) {
    Write-Host "Descargando herramientas necesarias..."
    New-Item -ItemType Directory -Force -Path $tempDir | Out-Null
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip
    Expand-Archive -Path $mavenZip -DestinationPath $tempDir -Force
}

Write-Host "Construyendo la aplicación y generando el archivo .exe..."
& $mvnCmd clean package -DskipTests

Write-Host "`n¡Éxito! El ejecutable ha sido generado."
Write-Host "Puedes encontrar el archivo AccuLab.exe en la carpeta target\"
Write-Host "`nPresiona cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
