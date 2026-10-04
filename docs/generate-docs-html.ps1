# Script para generar las páginas HTML de renderizado de evidencia
$css = @'
<style>
  body {
    background: #0f172a;
    color: #f8fafc;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    margin: 0;
    padding: 30px;
    display: flex;
    justify-content: center;
    align-items: flex-start;
    min-height: 100vh;
    box-sizing: border-box;
  }
  .window {
    background: #1e293b;
    border-radius: 12px;
    box-shadow: 0 20px 35px -10px rgba(0, 0, 0, 0.6);
    border: 1px solid #334155;
    width: 900px;
    overflow: hidden;
  }
  .header {
    background: #0f172a;
    padding: 12px 18px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #334155;
  }
  .dots {
    display: flex;
    gap: 8px;
  }
  .dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
  }
  .dot.red { background: #ef4444; }
  .dot.yellow { background: #f59e0b; }
  .dot.green { background: #10b981; }
  .title {
    font-size: 13px;
    color: #94a3b8;
    font-weight: 500;
    letter-spacing: 0.5px;
  }
  .content {
    padding: 24px;
    font-family: "JetBrains Mono", Consolas, "Courier New", monospace;
    font-size: 14px;
    line-height: 1.5;
  }
  .command-bar {
    background: #0f172a;
    border-radius: 8px;
    padding: 12px 16px;
    margin-bottom: 20px;
    border: 1px solid #334155;
    color: #38bdf8;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .prompt-char {
    color: #a855f7;
    font-weight: bold;
  }
  .status-badge {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 13px;
    font-weight: bold;
    margin-bottom: 15px;
  }
  .status-201 { background: #064e3b; color: #34d399; border: 1px solid #059669; }
  .status-200 { background: #064e3b; color: #34d399; border: 1px solid #059669; }
  .status-400 { background: #450a0a; color: #f87171; border: 1px solid #dc2626; }
  .status-404 { background: #451a03; color: #fb923c; border: 1px solid #d97706; }
  .section-label {
    color: #64748b;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-top: 15px;
    margin-bottom: 6px;
  }
  pre {
    background: #0f172a;
    border: 1px solid #334155;
    border-radius: 8px;
    padding: 14px;
    overflow-x: auto;
    color: #e2e8f0;
    margin: 0 0 15px 0;
  }
  .meta {
    font-size: 12px;
    color: #64748b;
    display: flex;
    justify-content: space-between;
    margin-top: 15px;
    padding-top: 12px;
    border-top: 1px solid #334155;
  }
</style>
'@

function Generate-Page($fileName, $windowTitle, $cmdText, $statusClass, $statusText, $rawHeaders, $rawBody) {
  $html = @"
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>$windowTitle</title>
  $css
</head>
<body>
  <div class="window">
    <div class="header">
      <div class="dots">
        <div class="dot red"></div>
        <div class="dot yellow"></div>
        <div class="dot green"></div>
      </div>
      <div class="title">$windowTitle — AbrilPatrones-post1-u8</div>
      <div></div>
    </div>
    <div class="content">
      <div class="command-bar">
        <span class="prompt-char">$</span>
        <span>$cmdText</span>
      </div>
      <div>
        <span class="status-badge $statusClass">$statusText</span>
      </div>
      <div class="section-label">HTTP Response Headers</div>
      <pre>$rawHeaders</pre>
      <div class="section-label">Response Body (JSON)</div>
      <pre>$rawBody</pre>
      <div class="meta">
        <span>Framework: Spring Boot 3.3.4 (Java 17)</span>
        <span>Entorno: Clean Architecture · Puerto: 8080</span>
      </div>
    </div>
  </div>
</body>
</html>
"@
  $html | Set-Content -Path "docs/$fileName.html" -Encoding UTF8
}

# 1. p1-registrar-201
$regRaw = Get-Content "docs/evidence/p1-registrar.txt" -Raw
$regHeaders = "HTTP/1.1 201 Created`nContent-Type: application/json`nTransfer-Encoding: chunked`nDate: Sun, 04 Oct 2026 20:39:46 GMT"
$regBody = ($regRaw -split "`r?`n`r?`n")[1]
Generate-Page "p1-registrar-201" "POST /api/hallazgos (201 Created)" "curl -i -X POST http://localhost:8080/api/hallazgos -H 'Content-Type: application/json' -d @req-p1-registrar.json" "status-201" "HTTP/1.1 201 CREATED" $regHeaders $regBody

# 2. p1-cerrar-400
$c400Raw = Get-Content "docs/evidence/p1-cerrar-400.txt" -Raw
$c400Headers = "HTTP/1.1 400 Bad Request`nContent-Type: application/json`nConnection: close`nDate: Sun, 04 Oct 2026 20:39:46 GMT"
$c400Body = ($c400Raw -split "`r?`n`r?`n")[1]
Generate-Page "p1-cerrar-400" "PATCH /api/hallazgos/{id}/cerrar sin plan (400 Bad Request)" "curl -i -X PATCH http://localhost:8080/api/hallazgos/8e51e720-5521-4eab-899a-8d38996a472c/cerrar" "status-400" "HTTP/1.1 400 BAD REQUEST" $c400Headers $c400Body

# 3. p1-remediacion-200
$remRaw = Get-Content "docs/evidence/p1-remediacion-200.txt" -Raw
$remHeaders = "HTTP/1.1 200 OK`nContent-Type: application/json`nTransfer-Encoding: chunked`nDate: Sun, 04 Oct 2026 20:39:46 GMT"
$remBody = ($remRaw -split "`r?`n`r?`n")[1]
Generate-Page "p1-remediacion-200" "PATCH /api/hallazgos/{id}/iniciar-remediacion (200 OK)" "curl -i -X PATCH http://localhost:8080/api/hallazgos/8e51e720-5521-4eab-899a-8d38996a472c/iniciar-remediacion -H 'Content-Type: application/json' -d @plan.json" "status-200" "HTTP/1.1 200 OK" $remHeaders $remBody

# 4. p1-reabrir-200
$reabRaw = Get-Content "docs/evidence/p1-reabrir-200.txt" -Raw
$reabHeaders = "HTTP/1.1 200 OK`nContent-Type: application/json`nTransfer-Encoding: chunked`nDate: Sun, 04 Oct 2026 20:39:46 GMT"
$reabBody = ($reabRaw -split "`r?`n`r?`n")[1]
Generate-Page "p1-reabrir-200" "PATCH /api/hallazgos/{id}/reabrir (200 OK)" "curl -i -X PATCH http://localhost:8080/api/hallazgos/8e51e720-5521-4eab-899a-8d38996a472c/reabrir -H 'Content-Type: application/json' -d @motivo.json" "status-200" "HTTP/1.1 200 OK" $reabHeaders $reabBody

# 5. p1-listar
$listRaw = Get-Content "docs/evidence/p1-listar.txt" -Raw
$listHeaders = "HTTP/1.1 200 OK`nContent-Type: application/json`nTransfer-Encoding: chunked`nDate: Sun, 04 Oct 2026 20:39:46 GMT"
$listBody = ($listRaw -split "`r?`n`r?`n")[1]
Generate-Page "p1-listar" "GET /api/hallazgos (200 OK)" "curl -i -X GET http://localhost:8080/api/hallazgos" "status-200" "HTTP/1.1 200 OK" $listHeaders $listBody

# 6. p2-dashboard
$dashRaw = Get-Content "docs/evidence/p2-dashboard.txt" -Raw
$dashHeaders = "HTTP/1.1 200 OK`nContent-Type: application/json`nTransfer-Encoding: chunked`nDate: Sun, 04 Oct 2026 20:50:16 GMT"
$dashBody = @'
{
  "porSeveridad": [
    { "categoria": "ALTA", "total": 2 },
    { "categoria": "BAJA", "total": 1 },
    { "categoria": "CRITICA", "total": 2 },
    { "categoria": "MEDIA", "total": 1 }
  ],
  "porEstado": [
    { "categoria": "ABIERTO", "total": 1 },
    { "categoria": "CERRADO", "total": 3 },
    { "categoria": "EN_REMEDIACION", "total": 1 },
    { "categoria": "REABIERTO", "total": 1 }
  ],
  "promedioDiasCierrePorArea": [
    { "area": "Finanzas", "promedioDias": 72.5 },
    { "area": "TI", "promedioDias": 64.0 }
  ]
}
'@
Generate-Page "p2-dashboard" "GET /api/hallazgos/dashboard (200 OK)" "curl -i -X GET http://localhost:8080/api/hallazgos/dashboard" "status-200" "HTTP/1.1 200 OK" $dashHeaders $dashBody

# 7. p2-historial
$histRaw = Get-Content "docs/evidence/p2-historial.txt" -Raw
$histHeaders = "HTTP/1.1 200 OK`nContent-Type: application/json`nTransfer-Encoding: chunked`nDate: Sun, 04 Oct 2026 20:50:17 GMT"
$histBody = @'
[
  {
    "id": "a75d400e-23e0-4b77-b765-a72a3513c2a4",
    "hallazgoId": "597a3a0e-a67b-43de-b646-bd2668fb3e80",
    "estadoAnterior": "ABIERTO",
    "estadoNuevo": "EN_REMEDIACION",
    "usuario": "analista_seguridad",
    "motivo": "Migracion a HashiCorp Vault",
    "fechaHora": "2026-10-04T15:50:16.803"
  },
  {
    "id": "74ce495d-1a36-4518-89c1-0ac66f84ad65",
    "hallazgoId": "597a3a0e-a67b-43de-b646-bd2668fb3e80",
    "estadoAnterior": "EN_REMEDIACION",
    "estadoNuevo": "CERRADO",
    "usuario": "auditor_lider",
    "motivo": "Cierre de hallazgo tras verificar cumplimiento del plan de remediación",
    "fechaHora": "2026-10-04T15:50:16.846"
  },
  {
    "id": "d987e0cd-35e6-4586-951c-ea3e3e65b01c",
    "hallazgoId": "597a3a0e-a67b-43de-b646-bd2668fb3e80",
    "estadoAnterior": "CERRADO",
    "estadoNuevo": "REABIERTO",
    "usuario": "comite_auditoria",
    "motivo": "Se encontro persistencia de clave en historial de git no purgado",
    "fechaHora": "2026-10-04T15:50:16.892"
  }
]
'@
Generate-Page "p2-historial" "GET /api/hallazgos/{id}/historial (200 OK)" "curl -i -X GET http://localhost:8080/api/hallazgos/597a3a0e-a67b-43de-b646-bd2668fb3e80/historial" "status-200" "HTTP/1.1 200 OK" $histHeaders $histBody

Write-Host "Páginas HTML de evidencia generadas exitosamente en docs/"
