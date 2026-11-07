# language: pt
@indicadores
Funcionalidade: Indicadores ambientais
  Como analista ESG
  Quero registrar o consumo de água e energia, os resíduos gerados e as emissões de CO2
  Para acompanhar mês a mês o impacto ambiental de cada empresa

  Contexto:
    Dado que estou autenticado como ADMIN
    E que existe uma empresa cadastrada

  @positivo
  Esquema do Cenário: Registrar o indicador do mês
    Quando eu registro <valor> "<unidade>" de "<tipo>" referente a "2026-10-01"
    Então a resposta tem status 201
    E o corpo segue o contrato "indicador"
    E o campo "tipo" vale "<tipo>"
    E o campo "valor" vale <valor>
    E o campo "empresaNome" vale o nome da empresa

    Exemplos:
      | tipo        | valor  | unidade |
      | AGUA        | 1320,5 | m3      |
      | ENERGIA     | 7800   | kWh     |
      | RESIDUOS    | 410    | kg      |
      | EMISSAO_CO2 | 95,75  | tCO2e   |

  @positivo
  Cenário: Consultar um indicador pelo id
    Dado que a empresa registrou 7800 "kWh" de "ENERGIA"
    Quando eu consulto o indicador
    Então a resposta tem status 200
    E o corpo segue o contrato "indicador"
    E o campo "valor" vale 7800
    E o campo "unidadeMedida" vale "kWh"

  @positivo
  Cenário: Listar os indicadores de uma empresa
    Dado que a empresa registrou 1320,5 "m3" de "AGUA"
    E que a empresa registrou 7800 "kWh" de "ENERGIA"
    Quando eu listo os indicadores da empresa
    Então a resposta tem status 200
    E o corpo segue o contrato "lista-indicadores"
    E a lista tem 2 itens

  @positivo
  Cenário: Filtrar os indicadores de resíduos
    Dado que a empresa registrou 410 "kg" de "RESIDUOS"
    Quando eu listo os indicadores do tipo "RESIDUOS"
    Então a resposta tem status 200
    E o corpo segue o contrato "lista-indicadores"
    E todos os itens têm "tipo" igual a "RESIDUOS"
    E o indicador registrado está na lista

  @positivo
  Cenário: Corrigir um valor lançado errado
    Dado que a empresa registrou 7800 "kWh" de "ENERGIA"
    Quando eu corrijo o valor do indicador para 6950,4
    Então a resposta tem status 200
    E o corpo segue o contrato "indicador"
    E o campo "valor" vale 6950,4

  @positivo
  Cenário: Excluir um indicador
    Dado que a empresa registrou 1320,5 "m3" de "AGUA"
    Quando eu excluo o indicador
    Então a resposta tem status 204
    E o indicador não é mais encontrado

  @negativo
  Cenário: Recusar valor negativo
    Quando eu registro -10 "kWh" de "ENERGIA" referente a "2026-10-01"
    Então a resposta tem status 400
    E o corpo segue o contrato "erros-validacao"
    E o campo "valor" tem o erro "O valor deve ser maior ou igual a zero"

  @negativo
  Cenário: Recusar indicador de uma empresa que não existe
    Quando eu registro um indicador para a empresa de id 999999
    Então a resposta tem status 404
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Empresa não encontrada com ID: 999999"

  @negativo
  Cenário: Recusar um tipo de indicador desconhecido no filtro
    Quando eu listo os indicadores do tipo "PLASTICO"
    Então a resposta tem status 400
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Valor inválido para o parâmetro 'tipo'."

  @negativo
  Cenário: Consultar um indicador que não existe
    Quando eu consulto "/api/indicadores/999999"
    Então a resposta tem status 404
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Indicador ESG não encontrado com ID: 999999"
