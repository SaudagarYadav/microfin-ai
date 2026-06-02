<#
.SYNOPSIS
    Refreshes the JWT access token using a stored refresh token.

.DESCRIPTION
    Calls POST {BaseUrl}/api/v1/auth/refresh and updates the
    script-scope $accessToken / $refreshToken variables.

.EXAMPLE
    . .\scripts\login.ps1
    . .\scripts\refresh.ps1
#>
[CmdletBinding()]
param(
    [string] $BaseUrl      = "http://localhost:8086",
    [string] $RefreshToken = $script:refreshToken
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($RefreshToken)) {
    throw "No refresh token provided. Run login.ps1 first or pass -RefreshToken."
}

$body = @{ refreshToken = $RefreshToken } | ConvertTo-Json

$response = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/refresh" `
    -Method POST `
    -Headers @{ "Content-Type" = "application/json" } `
    -Body $body

$script:accessToken  = $response.accessToken
$script:refreshToken = $response.refreshToken

$response | ConvertTo-Json -Depth 5
Write-Host "`nRotated. New access token in `$accessToken." -ForegroundColor Green
