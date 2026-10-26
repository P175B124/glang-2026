# Runs every example and compares its output with <name>.expected
Set-Location $PSScriptRoot
$glang = Join-Path $PSScriptRoot '..\bin\glang.bat'

$passed = 0
$failed = 0
foreach ($f in Get-ChildItem -Filter *.glang | Sort-Object Name) {
    $expectedFile = [IO.Path]::ChangeExtension($f.FullName, '.expected')
    $actual = (cmd /c "`"$glang`" `"$($f.Name)`" 2>&1" | Out-String).Replace("`r", '').TrimEnd()
    $expected = $null
    if (Test-Path $expectedFile) {
        $expected = (Get-Content -Raw $expectedFile).Replace("`r", '').TrimEnd()
    }
    if ($actual -eq $expected) {
        "PASS  $($f.Name)"
        $passed++
    } else {
        "FAIL  $($f.Name)"
        $actual -split "`n" | ForEach-Object { "      $_" }
        $failed++
    }
}

""
"$passed passed, $failed failed"
if ($failed -gt 0) { exit 1 }
