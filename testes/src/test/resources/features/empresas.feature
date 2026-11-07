# language: pt
@empresas
Funcionalidade: Cadastro de empresas
  Como analista ESG
  Quero manter o cadastro das empresas acompanhadas pelo EcoTrack
  Para lançar os indicadores e as ações ESG de cada uma

  Contexto:
    Dado que estou autenticado como ADMIN

  @positivo
  Cenário: Cadastrar uma empresa
    Quando eu cadastro a empresa:
      | nome   | Reciclar Embalagens LTDA |
      | setor  | Reciclagem               |
      | cidade | Santos                   |
      | estado | sp                       |
    Então a resposta tem status 201
    E o corpo segue o contrato "empresa"
    E o campo "nome" vale "Reciclar Embalagens LTDA"
    E o campo "estado" vale "SP"
    E a empresa aparece na listagem de empresas

  @positivo
  Cenário: Consultar uma empresa pelo id
    Dado que existe uma empresa cadastrada
    Quando eu consulto a empresa
    Então a resposta tem status 200
    E o corpo segue o contrato "empresa"
    E o corpo traz os dados da empresa cadastrada

  @positivo
  Cenário: Listar as empresas
    Dado que existe uma empresa cadastrada
    Quando eu consulto "/api/empresas"
    Então a resposta tem status 200
    E o corpo segue o contrato "lista-empresas"
    E a empresa cadastrada está na lista

  @positivo
  Cenário: Mudar o setor e a cidade de uma empresa
    Dado que existe uma empresa cadastrada
    Quando eu altero a empresa para o setor "Energia solar" e a cidade "Campinas"
    Então a resposta tem status 200
    E o corpo segue o contrato "empresa"
    E o campo "setor" vale "Energia solar"
    E o campo "cidade" vale "Campinas"

  @positivo
  Cenário: Excluir uma empresa sem registros ligados a ela
    Dado que existe uma empresa cadastrada
    Quando eu excluo a empresa
    Então a resposta tem status 204
    E a empresa não é mais encontrada

  @negativo @governanca
  Cenário: Recusar um CNPJ que já está cadastrado
    Dado que existe uma empresa cadastrada
    Quando eu cadastro outra empresa com o mesmo CNPJ
    Então a resposta tem status 400
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Já existe uma empresa cadastrada com este CNPJ."

  @negativo @governanca
  Cenário: Impedir a exclusão de uma empresa que tem indicadores
    Dado que existe uma empresa cadastrada
    E que a empresa registrou 4200 "kWh" de "ENERGIA"
    Quando eu excluo a empresa
    Então a resposta tem status 409
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Operação viola integridade dos dados. Verifique se o registro possui dependências."

  @negativo
  Cenário: Consultar uma empresa que não existe
    Quando eu consulto a empresa de id 999999
    Então a resposta tem status 404
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Empresa não encontrada com ID: 999999"

  @negativo
  Cenário: Consultar uma empresa com id que não é número
    Quando eu consulto "/api/empresas/abc"
    Então a resposta tem status 400
    E o corpo segue o contrato "erro"
    E a mensagem de erro é "Valor inválido para o parâmetro 'id'."

  @negativo @inclusao
  Esquema do Cenário: Dizer qual campo do cadastro está errado
    Quando eu cadastro uma empresa com "<campo>" igual a "<valor>"
    Então a resposta tem status 400
    E o corpo segue o contrato "erros-validacao"
    E o campo "<campo>" tem o erro "<mensagem>"

    Exemplos:
      | campo  | valor          | mensagem                                        |
      | cnpj   | 12345678000190 | O CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX |
      | estado | SPX            | O estado deve conter 2 caracteres               |
      | nome   |                | O nome é obrigatório                            |
