$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$envFile = Join-Path $root ".env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#=]+?)\s*=\s*(.*?)\s*$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
        }
    }
}

Write-Host "=== Running SonarQube scan for Backend ==="
Set-Location "$root\backend"
& ./mvnw.cmd clean verify sonar:sonar `
  "-Dsonar.host.url=http://localhost:9000" `
  "-Dsonar.token=$env:SONAR_TOKEN"
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Backend SonarQube scan failed with exit code $LASTEXITCODE."
    exit $LASTEXITCODE
}

Write-Host "=== Running SonarQube scan for Frontend ==="
Push-Location "$root\Frontend"
cmd /c "npm test -- --coverage --coverage-reporters lcov --coverage-reporters text"
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Frontend tests failed with exit code $LASTEXITCODE."
    exit $LASTEXITCODE
}
npx sonar-scanner
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Frontend SonarQube scan failed with exit code $LASTEXITCODE."
    exit $LASTEXITCODE
}
Pop-Location

Write-Host "=== SonarQube scans completed ==="
Write-Host "Backend: http://localhost:9000/dashboard?id=ecommerce-api"
Write-Host "Frontend: http://localhost:9000/dashboard?id=ecommerce-web"
Set-Location $root
