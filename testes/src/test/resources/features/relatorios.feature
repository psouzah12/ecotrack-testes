# language: pt
@relatorios
Funcionalidade: Relatórios ESG
  Como diretoria da empresa
  Quero ver o resumo dos indicadores e o peso de cada ação no impacto total
  Para prestar contas com números que batem com o que foi lançado

  Contexto:
    Dado que estou autenticado como ADMIN
    E que existe uma empresa cadastrada
    E que a empresa registrou 7800 "kWh" de "ENERGIA"
    E que a empresa registrou 410 "kg" de "RESIDUOS"
    E que a empresa tem uma ação de "EFICIENCIA_ENERGETICA" com status "CONCLUIDA" e impacto de 300
    E que a empresa tem uma ação de "RECICLAGEM" com status "PLANEJADA" e impacto de 700

  @positivo
  Cenário: Resumo de uma empresa
    Quando eu consulto o resumo da empresa
    Então a resposta tem status 200
    E o corpo segue o contrato "resumo"
    E o resumo mostra:
      | totalEmpresas        | 1    |
      | totalIndicadores     | 2    |
      | totalAcoes           | 2    |
      | acoesConcluidas      | 1    |
      | impactoTotalEstimado | 1000 |

  @positivo
  Cenário: Peso de uma ação no impacto da empresa
    Quando eu consulto o impacto da ação de "EFICIENCIA_ENERGETICA"
    Então a resposta tem status 200
    E o corpo segue o contrato "impacto"
    E o campo "impactoEstimado" vale 300
    E o campo "impactoTotalEmpresa" vale 1000
    E o campo "percentualSobreTotal" vale 30

  @positivo @governanca
  Cenário: O resumo geral bate com os registros da API
    Quando eu consulto o resumo geral
    Então a resposta tem status 200
    E o corpo segue o contrato "resumo"
    E os totais do resumo geral batem com as listagens de empresas, indicadores e ações

  @negativo
  Cenário: Resumo de uma empresa que não existe
    Quando eu consulto o resumo da empresa de id 999999
    Então a resposta tem status 404
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Empresa não encontrada com ID: 999999"

  @negativo
  Cenário: Impacto de uma ação que não existe
    Quando eu consulto o impacto da ação de id 999999
    Então a resposta tem status 404
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Ação ESG não encontrada com ID: 999999"
