# Build script untuk Democracy Core Plugin
Write-Host "====================================" -ForegroundColor Cyan
Write-Host "Building Democracy Core Plugin v1.3.0" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

$mavenPath = "C:\Users\mohza\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\maven\latest\bin\mvn.cmd"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

Write-Host "Maven: $mavenPath" -ForegroundColor Yellow
Write-Host "Java: $env:JAVA_HOME" -ForegroundColor Yellow
Write-Host ""

& $mavenPath clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Green
    Write-Host "BUILD SUCCESS!" -ForegroundColor Green
    Write-Host "====================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Output JAR: target\Democracy-Core-Plugin-1.3.0.jar" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Red
    Write-Host "BUILD FAILED!" -ForegroundColor Red
    Write-Host "====================================" -ForegroundColor Red
    Write-Host ""
}
