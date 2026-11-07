# Container que roda os cenários BDD contra a API (usado pelo docker-compose.yml).
# Os argumentos depois do nome do serviço vão para o Maven:
#   docker compose run --rm --build testes -Dcucumber.filter.tags=@negativo
# O --build refaz a imagem quando um .feature, .java ou schema muda.
FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /testes

# dependências numa camada própria: só baixa de novo quando o pom.xml muda
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src

ENV ECOTRACK_BASE_URL=http://api:8080
ENTRYPOINT ["mvn", "-B", "-ntp", "test"]
