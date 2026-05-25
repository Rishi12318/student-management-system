param()

$ErrorActionPreference = 'Stop'
$mavenVersion = '3.9.5'
$mavenBase = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries"
$zipName = "apache-maven-$mavenVersion-bin.zip"
$zipUrl = "$mavenBase/$zipName"
$destDir = Join-Path $PSScriptRoot '..' | Resolve-Path -Relative
$mavenDir = Join-Path $destDir "apache-maven-$mavenVersion"

if (-not (Test-Path $mavenDir)) {
    Write-Host "Downloading Maven $mavenVersion..."
    $zipPath = Join-Path $env:TEMP $zipName
    Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath -UseBasicParsing
    Write-Host "Extracting to project folder..."
    Expand-Archive -Path $zipPath -DestinationPath $destDir -Force
    Remove-Item $zipPath -Force
}

$mvnCmd = Join-Path $mavenDir 'bin\mvn.cmd'
if (-not (Test-Path $mvnCmd)) {
    Write-Error "Maven executable not found after extraction: $mvnCmd"
    exit 1
}

Write-Host "Running 'mvn -v' using portable Maven..."
& $mvnCmd -v
