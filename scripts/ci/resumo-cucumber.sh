#!/usr/bin/env bash
# Tabela por funcionalidade a partir do cucumber.json, para o resumo do GitHub Actions.
# Uso: scripts/ci/resumo-cucumber.sh testes/target/cucumber/cucumber.json >> "$GITHUB_STEP_SUMMARY"
set -euo pipefail
JSON="${1:?informe o caminho do cucumber.json}"

if [ ! -f "$JSON" ]; then
  echo "### Cenários BDD"
  echo
  echo "Os testes não chegaram a rodar (sem $JSON)."
  exit 0
fi

echo "### Cenários BDD"
echo
echo "| Funcionalidade | Cenários | Passaram | Falharam |"
echo "|---|---:|---:|---:|"
jq -r '
  .[] | . as $f
  | [ .elements[] | select(.type == "scenario")
      | [ (.before // [])[], .steps[], (.after // [])[] ] | all(.result.status == "passed") ] as $r
  | "| \($f.name) | \($r | length) | \($r | map(select(.)) | length) | \($r | map(select(. | not)) | length) |"
' "$JSON"

echo
jq -r '
  [ .[].elements[] | select(.type == "scenario")
    | select([ (.before // [])[], .steps[], (.after // [])[] ] | any(.result.status != "passed"))
    | "- \(.name)" ]
  | if length == 0 then "Todos os cenários passaram." else "Cenários com falha:\n" + join("\n") end
' "$JSON"
