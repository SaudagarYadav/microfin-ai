# Auth Service — Helper Scripts

PowerShell helpers for exercising the MicroFin Auth Service from a local shell.

## Files

| Script        | Purpose                                                           |
|---------------|-------------------------------------------------------------------|
| `login.ps1`   | `POST /api/v1/auth/login` and capture `accessToken` / `refreshToken`. |
| `refresh.ps1` | `POST /api/v1/auth/refresh` and rotate the stored tokens.         |

> **Tip:** dot-source the scripts (note the leading `. `) so the
> `$accessToken` and `$refreshToken` variables remain available in your
> current PowerShell session.

## Quick start

```powershell
# From service/auth-service
. .\scripts\login.ps1                         # uses defaults
. .\scripts\login.ps1 -VerifyMe               # also calls /me
. .\scripts\login.ps1 -Username "alice" `
                      -Password "Secret#123"  # override credentials

# Reuse the captured token
Invoke-RestMethod http://localhost:8086/api/v1/auth/me `
    -Headers @{ Authorization = "Bearer $accessToken" }

# Rotate the token
. .\scripts\refresh.ps1
```

## Parameters (login.ps1)

| Parameter   | Default                          |
|-------------|----------------------------------|
| `-BaseUrl`  | `http://localhost:8086`          |
| `-Username` | `saudagar.yadav@gmail.com`       |
| `-Password` | `password` *(local dev only)*    |
| `-VerifyMe` | switch — also calls `/auth/me`   |

⚠️ Defaults are for **local development only**. Do not commit real
credentials and do not use these defaults against `dev`/`qa`/`prod`.
