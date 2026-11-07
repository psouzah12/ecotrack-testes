# language: pt
@acoes
Funcionalidade: Ações ESG
  Como gestor de sustentabilidade
  Quero planejar ações ESG e atualizar o andamento de cada uma
  Para saber o que já foi feito e qual impacto esperar

  Contexto:
    Dado que estou autenticado como ADMIN
    E que existe uma empresa cadastrada

  @positivo
  Cenário: Planejar uma ação de coleta seletiva
    Quando eu cadastro a ação:
      | titulo          | Coleta seletiva no refeitório                        |
      | descricao       | Lixeiras separadas e coleta semanal pela cooperativa |
      | categoria       | RECICLAGEM                                           |
      | status          | PLANEJADA                                            |
      | impactoEstimado | 350,00                                               |
      | dataInicio      | 2026-11-16                                           |
      | dataFimPrevista | 2027-02-16                                           |
    Então a resposta tem status 201
    E o corpo segue o contrato "acao"
    E o campo "categoria" vale "RECICLAGEM"
    E o campo "status" vale "PLANEJADA"
    E o campo "empresaNome" vale o nome da empresa

  @positivo
  Esquema do Cenário: Atualizar o andamento de uma ação
    Dado que a empresa tem uma ação de "RECICLAGEM" com status "PLANEJADA" e impacto de 350
    Quando eu mudo o status da ação para "<novo status>"
    Então a resposta tem status 200
    E o corpo segue o contrato "acao"
    E o campo "status" vale "<novo status>"

    Exemplos:
      | novo status  |
      | EM_ANDAMENTO |
      | CONCLUIDA    |
      | CANCELADA    |

  @positivo
  Cenário: Consultar uma ação pelo id
    Dado que a empresa tem uma ação de "EFICIENCIA_ENERGETICA" com status "EM_ANDAMENTO" e impacto de 1200
    Quando eu consulto a ação
    Então a resposta tem status 200
    E o corpo segue o contrato "acao"
    E o campo "titulo" vale "Troca das lâmpadas do galpão por LED"
    E o campo "impactoEstimado" vale 1200

  @positivo
  Cenário: Listar as ações de uma empresa
    Dado que a empresa tem uma ação de "RECICLAGEM" com status "PLANEJADA" e impacto de 350
    E que a empresa tem uma ação de "REDUCAO_CONSUMO" com status "EM_ANDAMENTO" e impacto de 800
    Quando eu listo as ações da empresa
    Então a resposta tem status 200
    E o corpo segue o contrato "lista-acoes"
    E a lista tem 2 itens

  @positivo
  Cenário: Filtrar as ações concluídas
    Dado que a empresa tem uma ação de "COMPENSACAO_CARBONO" com status "CONCLUIDA" e impacto de 500
    Quando eu listo as ações com status "CONCLUIDA"
    Então a resposta tem status 200
    E o corpo segue o contrato "lista-acoes"
    E todos os itens têm "status" igual a "CONCLUIDA"
    E a ação cadastrada está na lista

  @positivo
  Cenário: Ampliar uma ação já cadastrada
    Dado que a empresa tem uma ação de "RECICLAGEM" com status "PLANEJADA" e impacto de 350
    Quando eu altero o título da ação para "Coleta seletiva em todas as unidades" e o impacto para 900
    Então a resposta tem status 200
    E o corpo segue o contrato "acao"
    E o campo "titulo" vale "Coleta seletiva em todas as unidades"
    E o campo "impactoEstimado" vale 900

  @positivo
  Cenário: Excluir uma ação
    Dado que a empresa tem uma ação de "EDUCACAO_AMBIENTAL" com status "CANCELADA" e impacto de 0
    Quando eu excluo a ação
    Então a resposta tem status 204
    E a ação não é mais encontrada

  @negativo
  Cenário: Recusar um status que não existe
    Dado que a empresa tem uma ação de "RECICLAGEM" com status "PLANEJADA" e impacto de 350
    Quando eu mudo o status da ação para "ARQUIVADA"
    Então a resposta tem status 400
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Corpo da requisição inválido ou mal formatado."

  @negativo @inclusao
  Cenário: Apontar todos os campos obrigatórios que faltam
    Quando eu cadastro uma ação só com a descrição
    Então a resposta tem status 400
    E o corpo segue o contrato "erros-validacao"
    E o campo "titulo" tem o erro "O título é obrigatório"
    E o campo "categoria" tem o erro "A categoria é obrigatória"
    E o campo "status" tem o erro "O status é obrigatório"
    E o campo "dataInicio" tem o erro "A data de início é obrigatória"

  @negativo
  Cenário: Mudar o status de uma ação que não existe
    Quando eu mudo o status da ação de id 999999 para "CONCLUIDA"
    Então a resposta tem status 404
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Ação ESG não encontrada com ID: 999999"
