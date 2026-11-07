# language: pt
@acesso @governanca
Funcionalidade: Acesso por perfil
  Como responsável pela governança dos dados ESG
  Quero que só o perfil ADMIN altere registros
  Para que os números publicados não sejam mudados por quem não tem permissão

  @positivo @eficiencia
  Esquema do Cenário: Consultar dados públicos sem login
    Dado que existe uma empresa cadastrada
    E que a empresa registrou 100 "m3" de "AGUA"
    E que a empresa tem uma ação de "RECICLAGEM" com status "PLANEJADA" e impacto de 350
    E que não estou autenticado
    Quando eu consulto "<endereço>"
    Então a resposta tem status 200
    E o corpo segue o contrato "<contrato>"
    E a resposta chega em menos de 2000 ms

    Exemplos:
      | endereço               | contrato          |
      | /api/empresas          | lista-empresas    |
      | /api/indicadores       | lista-indicadores |
      | /api/acoes-esg         | lista-acoes       |
      | /api/relatorios/resumo | resumo            |

  @positivo
  Cenário: Perfil USER consulta os relatórios
    Dado que estou autenticado como USER
    Quando eu consulto "/api/relatorios/resumo"
    Então a resposta tem status 200
    E o corpo segue o contrato "resumo"

  @negativo
  Cenário: Cadastro sem login
    Dado que não estou autenticado
    Quando eu tento cadastrar uma empresa
    Então a resposta tem status 401
    E a resposta pede autenticação Basic

  @negativo
  Cenário: Cadastro com senha errada
    Dado que estou autenticado como ADMIN com a senha "senha-errada"
    Quando eu tento cadastrar uma empresa
    Então a resposta tem status 401
    E a resposta pede autenticação Basic

  @negativo
  Esquema do Cenário: Perfil USER não altera dados
    Dado que estou autenticado como USER
    Quando eu envio <método> para "<endereço>"
    Então a resposta tem status 403
    E o corpo segue o contrato "acesso-negado"

    Exemplos:
      | método | endereço                |
      | POST   | /api/empresas           |
      | PUT    | /api/indicadores/1      |
      | PATCH  | /api/acoes-esg/1/status |
      | DELETE | /api/empresas/1         |
