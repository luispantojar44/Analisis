$ErrorActionPreference = "Stop"
Set-Location -Path $PSScriptRoot

$mavenVersion = "3.9.6"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$tempDir = "$env:TEMP\maven"
$mavenZip = "$tempDir\maven.zip"
$mavenDir = "$tempDir\apache-maven-$mavenVersion"
$mvnCmd = "$mavenDir\bin\mvn.cmd"

if (-not (Test-Path $mvnCmd)) {
    Write-Host "Detecté que no tienes Maven instalado. Descargando Maven temporalmente..."
    New-Item -ItemType Directory -Force -Path $tempDir | Out-Null
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip
    Write-Host "Extrayendo Maven..."
    Expand-Archive -Path $mavenZip -DestinationPath $tempDir -Force
}

Write-Host "Ejecutando la aplicación AccuLab (Compilando y descargando dependencias, esto tomará unos segundos)..."
& $mvnCmd clean javafx:run
