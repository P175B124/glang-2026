#!/bin/sh
# Runs every example and compares its output with <name>.expected
cd "$(dirname "$0")" || exit 2
GLANG=../bin/glang

passed=0
failed=0
for f in *.glang; do
    name="${f%.glang}"
    actual="$("$GLANG" "$f" 2>&1)"
    if [ -f "$name.expected" ] && [ "$actual" = "$(cat "$name.expected")" ]; then
        echo "PASS  $f"
        passed=$((passed + 1))
    else
        echo "FAIL  $f"
        echo "$actual" | sed 's/^/      /'
        failed=$((failed + 1))
    fi
done

echo
echo "$passed passed, $failed failed"
[ "$failed" -eq 0 ]
