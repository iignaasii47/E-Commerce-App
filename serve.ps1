$root = Split-Path -Parent $MyInvocation.MyCommand.Path

$pgRunning = Test-NetConnection -ComputerName localhost -Port 5432 -InformationLevel Quiet -WarningAction SilentlyContinue

if (-not $pgRunning) {
    Write-Host "PostgreSQL is not running. Starting..."
    pg_ctl start
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Failed to start PostgreSQL (pg_ctl exited with code $LASTEXITCODE). Aborting."
        exit 1
    }
    Write-Host "Waiting 5 seconds for PostgreSQL to be ready..."
    Start-Sleep -Seconds 5
} else {
    Write-Host "PostgreSQL is already running."
}

Start-Process powershell -ArgumentList '-NoExit', '-Command', "Set-Location '$root\backend'; ./serve-api.ps1"
Start-Process powershell -ArgumentList '-NoExit', '-Command', "Set-Location '$root\frontend'; npx.cmd ng serve"
