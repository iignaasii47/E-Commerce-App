$backendRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $backendRoot
$envFile = Join-Path $repoRoot ".env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#=]+?)\s*=\s*(.*?)\s*$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
        }
    }
}

Set-Location $backendRoot

Write-Host "Running Flyway migrations..."
& .\mvnw.cmd flyway:migrate -B
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Flyway migration failed. Aborting."
    exit 1
}

Write-Host "Starting Spring Boot with DevTools hot-reload..."
$springProc = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -NoNewWindow -PassThru

Start-Sleep -Seconds 6

$srcPath = Join-Path $backendRoot "src\main\java"
if (-not (Test-Path $srcPath)) {
    Write-Host "[DevTools] ERROR: Source directory not found at $srcPath"
    exit 1
}

$lastSnapshot = @{ }
Get-ChildItem -Path $srcPath -Recurse -Filter "*.java" -ErrorAction SilentlyContinue | ForEach-Object {
    $lastSnapshot[$_.FullName] = $_.LastWriteTime
}

Write-Host ""
Write-Host "[DevTools] Auto-reload active. Save a .java file to trigger restart."
Write-Host "[DevTools] Press Ctrl+C to stop."
Write-Host ""

$debounceMs = 2500
$nextCompileAllowed = [DateTime]::MinValue

while (-not $springProc.HasExited) {
    Start-Sleep -Seconds 2
    $changed = $false

    Get-ChildItem -Path $srcPath -Recurse -Filter "*.java" -ErrorAction SilentlyContinue | ForEach-Object {
        $key = $_.FullName
        if (-not $lastSnapshot.ContainsKey($key) -or $lastSnapshot[$key] -ne $_.LastWriteTime) {
            $changed = $true
            $lastSnapshot[$key] = $_.LastWriteTime
        }
    }

    if ($changed -and ([DateTime]::Now -ge $nextCompileAllowed)) {
        $nextCompileAllowed = ([DateTime]::Now).AddMilliseconds($debounceMs)
        Write-Host "> [DevTools] Change detected, compiling..."
        Push-Location $backendRoot
        $compileResult = & .\mvnw.cmd compile -q 2>&1
        Pop-Location
        if ($LASTEXITCODE -eq 0) {
            Write-Host "> [DevTools] Compiled successfully. DevTools will restart the app..."
        } else {
            Write-Host "> [DevTools] Compilation failed. Fix errors and save again."
            if ($compileResult) { Write-Host $compileResult }
        }
    }
}

Write-Host "[DevTools] Spring Boot process exited."
