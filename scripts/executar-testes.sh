#!/usr/bin/env bash
# Roda os cenários BDD do começo ao fim, só com Docker: sobe o Oracle e a API,
# executa os testes no container e derruba o ambiente.
#
# Uso: scripts/executar-testes.sh [argumentos do Maven]
#   scripts/executar-testes.sh
#   scripts/executar-testes.sh -Dcucumber.filter.tags=@negativo
#   MANTER_AMBIENTE=1 scripts/executar-testes.sh    (não derruba no fim)
#
# Relatórios: testes/target/cucumber/relatorio.html, cucumber.json e cucumber.xml
set -euo pipefail
cd "$(dirname "$0")/.."

# derruba o ambiente também quando a subida falha ou a execução é interrompida
derrubar() {
  if [ "${MANTER_AMBIENTE:-0}" != 1 ]; then
    echo "==> Derrubando o ambiente"
    docker compose down -v
  fi
}
trap derrubar EXIT

echo "==> Subindo Oracle e API (na primeira vez leva uns 2 minutos)"
docker compose up -d --wait oracle api
docker compose ps --format 'table {{.Service}}\t{{.Image}}\t{{.Status}}'
echo "==> API em http://localhost:${APP_PORT:-8080}"

echo "==> Rodando os cenários"
status=0
docker compose run --rm --build testes "$@" || status=$?

# o container roda como root: devolve testes/target para o usuário da máquina (importa no Linux)
docker compose run --rm --no-deps --entrypoint chown testes -R "$(id -u):$(id -g)" /testes/target

echo "==> Relatório em testes/target/cucumber/relatorio.html"
exit "$status"
