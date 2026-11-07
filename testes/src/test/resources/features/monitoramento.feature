# language: pt
@monitoramento @governanca
Funcionalidade: Saúde e versão da API
  Como equipe de operação
  Quero saber se a API e o banco estão no ar e qual versão está rodando
  Para rastrear o que foi publicado em cada ambiente

  Contexto:
    Dado que não estou autenticado

  @positivo
  Cenário: API e banco no ar
    Quando eu consulto "/actuator/health"
    Então a resposta tem status 200
    E o corpo segue o contrato "health"
    E o campo "status" vale "UP"
    E o campo "components.db.status" vale "UP"

  @positivo
  Cenário: Versão e commit da imagem publicados
    Quando eu consulto "/actuator/info"
    Então a resposta tem status 200
    E o corpo segue o contrato "info"
    E o campo "app.nome" vale "EcoTrack API"
