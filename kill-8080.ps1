$pid = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess
if ($pid) {
    Write-Host "Killing process on port 8080 (PID $pid)..."
    Stop-Process -Id $pid -Force
} else {
    Write-Host "No process found on port 8080."
}
