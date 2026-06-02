<#
.SYNOPSIS
    Logs into the MicroFin Auth Service and prints / exports JWT tokens.

.DESCRIPTION
    Calls POST {BaseUrl}/api/v1/auth/login with the supplied credentials,
    prints the response, and exposes $accessToken / $refreshToken as
    script-scope variables for reuse in the same PowerShell session.

    Optionally calls /api/v1/auth/me to verify the token.

.PARAMETER BaseUrl
    Base URL of the auth-service. Defaults to http://localhost:8086.

.PARAMETER Username
    Username or email. Defaults to saudagar.yadav@gmail.com.

.PARAMETER Password
    Password. Defaults to "password" (local dev only).

.PARAMETER VerifyMe
    If set, also calls GET /api/v1/auth/me with the new access token.

.EXAMPLE
    # Use defaults (local dev)
    . .\scripts\login.ps1

.EXAMPLE
    # Override credentials
    . .\scripts\login.ps1 -Username "alice" -Password "Secret#123" -VerifyMe

.NOTES
    Dot-source the script (note the leading ". ") so $accessToken and
    $refreshToken remain available in your shell after it runs.
#>
[CmdletBinding()]
param(
    [string] $BaseUrl  = "http://localhost:8086",
    [string] $Username = "saudagar.yadav@gmail.com",
    [string] $Password = "password",
    [switch] $VerifyMe
)

$ErrorActionPreference = "Stop"

$loginUrl = "$BaseUrl/api/v1/auth/login"
$headers  = @{ "Content-Type" = "application/json" }
$body     = @{
    username = $Username
    password = $Password
} | ConvertTo-Json

Write-Host "POST $loginUrl  (user=$Username)" -ForegroundColor Cyan

try {
    $response = Invoke-RestMethod -Uri $loginUrl -Method POST -Headers $headers -Body $body
}
catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
        Write-Host $_.ErrorDetails.Message -ForegroundColor Red
    }
    throw
}

# Expose tokens to the caller's session (when dot-sourced)
$script:accessToken  = $response.accessToken
$script:refreshToken = $response.refreshToken

Write-Host "`n--- Login Response ---" -ForegroundColor Green
$response | ConvertTo-Json -Depth 5

Write-Host "`nAccess Token:  $accessToken"
Write-Host "Refresh Token: $refreshToken"

if ($VerifyMe) {
    Write-Host "`nGET $BaseUrl/api/v1/auth/me" -ForegroundColor Cyan
    $me = Invoke-RestMethod -Uri "$BaseUrl/api/v1/auth/me" `
        -Headers @{ Authorization = "Bearer $accessToken" }
    $me | ConvertTo-Json -Depth 5
}
