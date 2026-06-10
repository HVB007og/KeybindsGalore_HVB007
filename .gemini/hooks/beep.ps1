# Play a two-tone alarm (Frequency, Duration in ms)
[console]::beep(800, 300)
[console]::beep(1200, 400)

# The CLI requires strict JSON output from hooks to proceed
Write-Output '{"decision": "allow"}'