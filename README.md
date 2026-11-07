# EcoTrack API - testes BDD

Paulo Gabriel de Souza, RM 566228

Testes automatizados da EcoTrack API ([ecotrack-devops](https://github.com/psouzah12/ecotrack-devops)). Os cenários estão escritos em Gherkin, em português, e rodam com Cucumber e REST Assured contra a API e o banco Oracle, que sobem pelo Docker Compose. Em cada resposta o teste confere o status e, quando há corpo, os campos e o contrato em JSON Schema.

São 56 cenários em 6 arquivos `.feature`, cobrindo as 21 rotas da API e as rotas `/actuator/health` e `/actuator/info`.

## Requisitos

- Docker com Docker Compose v2 (basta isso para rodar o script)
- JDK 21 e Maven 3.9, só se quiser rodar o Maven fora do container

A API usada é a imagem `ghcr.io/psouzah12/ecotrack-api:sha-92e76ea` (versão 1.1.0), publicada pelo pipeline do projeto ecotrack-devops. O banco é o `gvenzl/oracle-free:23-slim`.

## Como rodar

```bash
scripts/executar-testes.sh
```

O script sobe o Oracle e a API, espera os dois ficarem prontos, roda os cenários num container com Maven e JDK 21 e derruba o ambiente no final. Na primeira vez leva uns 3 minutos por causa do download das imagens. O relatório do Cucumber fica em `testes/target/cucumber/relatorio.html`.

Para rodar só uma parte dos cenários, passe uma tag:

```bash
scripts/executar-testes.sh -Dcucumber.filter.tags=@negativo
scripts/executar-testes.sh -Dcucumber.filter.tags=@empresas
```

Com `MANTER_AMBIENTE=1` na frente do comando, o Oracle e a API continuam no ar depois dos testes.

Os mesmos passos sem o script:

```bash
docker compose up -d --wait oracle api
docker compose run --rm --build testes
docker compose down -v
```

### Com Maven na máquina

Com a API no ar (pelo `docker compose up` acima ou em outro endereço):

```bash
cd testes
mvn test
```

| Variável | Padrão |
|---|---|
| `ECOTRACK_BASE_URL` | `http://localhost:8080` |
| `ECOTRACK_ADMIN_USER` / `ECOTRACK_ADMIN_PASSWORD` | `admin@ecotrack.com` / `admin123` |
| `ECOTRACK_USER` / `ECOTRACK_USER_PASSWORD` | `user@ecotrack.com` / `user123` |

As variáveis também podem ser passadas com `-D`, por exemplo `mvn test -DECOTRACK_BASE_URL=http://localhost:8081`.

### GitHub Actions

O workflow `.github/workflows/testes-bdd.yml` roda a cada push na branch `main` que mexe nos testes (mudanças só em arquivos `.md` não disparam), em pull requests para a `main` e manualmente pela aba Actions. Ele sobe o Oracle e a API com o mesmo `docker-compose.yml`, roda `mvn test`, mostra uma tabela com o resultado na aba Summary e guarda o relatório do Cucumber como artefato (`relatorio-cucumber`).

## Estrutura

```
ecotrack-testes/
├── .github/workflows/testes-bdd.yml
├── docker-compose.yml
├── scripts/
│   ├── executar-testes.sh
│   └── ci/resumo-cucumber.sh
└── testes/
    ├── Dockerfile
    ├── pom.xml
    └── src/test/
        ├── java/br/com/fiap/ecotrack/bdd/   passos e configuração dos testes
        └── resources/
            ├── features/                    cenários em Gherkin
            ├── schemas/                     contratos em JSON Schema
            └── junit-platform.properties
```

## Cenários

| Arquivo | Cenários | Assunto |
|---|---:|---|
| `empresas.feature` | 12 | cadastro, consulta, listagem, alteração e exclusão de empresas |
| `indicadores.feature` | 13 | indicadores de água, energia, resíduos e CO2 |
| `acoes-esg.feature` | 13 | ações ESG e mudança de status |
| `relatorios.feature` | 5 | resumo por empresa, resumo geral e impacto das ações |
| `acesso.feature` | 11 | leitura pública e escrita só com o perfil ADMIN |
| `monitoramento.feature` | 2 | health e versão da API |

Cada arquivo tem cenários de sucesso (tag `@positivo`) e de erro (tag `@negativo`).
