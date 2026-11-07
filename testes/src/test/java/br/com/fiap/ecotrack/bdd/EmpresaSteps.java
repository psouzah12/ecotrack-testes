package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EmpresaSteps {

    private static final String EMPRESAS = "/api/empresas";

    private final Cenario cenario;

    public EmpresaSteps(Cenario cenario) {
        this.cenario = cenario;
    }

    @Dado("que existe uma empresa cadastrada")
    public void queExisteUmaEmpresaCadastrada() {
        cenario.empresa = cenario.criar(EMPRESAS, Dados.empresa("Verde Vale Reciclagem"));
    }

    @Quando("eu cadastro a empresa:")
    public void euCadastroAEmpresa(Map<String, String> campos) {
        Map<String, Object> dados = Dados.empresa(campos.get("nome"));
        dados.putAll(campos);
        cenario.enviar(Method.POST, EMPRESAS, dados);
    }

    @Quando("eu cadastro outra empresa com o mesmo CNPJ")
    public void euCadastroOutraEmpresaComOMesmoCnpj() {
        Map<String, Object> dados = Dados.empresa("Outra Empresa LTDA");
        dados.put("cnpj", cenario.empresa.dados().get("cnpj"));
        cenario.enviar(Method.POST, EMPRESAS, dados);
    }

    @Quando("eu cadastro uma empresa com {string} igual a {string}")
    public void euCadastroUmaEmpresaCom(String campo, String valor) {
        Map<String, Object> dados = Dados.empresa("Empresa com dado inválido");
        dados.put(campo, valor);
        cenario.enviar(Method.POST, EMPRESAS, dados);
    }

    @Quando("eu consulto a empresa")
    public void euConsultoAEmpresa() {
        cenario.get(EMPRESAS + "/" + cenario.empresa.id());
    }

    @Quando("eu consulto a empresa de id {long}")
    public void euConsultoAEmpresaDeId(long id) {
        cenario.get(EMPRESAS + "/" + id);
    }

    @Quando("eu altero a empresa para o setor {string} e a cidade {string}")
    public void euAlteroAEmpresa(String setor, String cidade) {
        Map<String, Object> dados = new LinkedHashMap<>(cenario.empresa.dados());
        dados.put("setor", setor);
        dados.put("cidade", cidade);
        cenario.enviar(Method.PUT, EMPRESAS + "/" + cenario.empresa.id(), dados);
    }

    @Quando("eu excluo a empresa")
    public void euExcluoAEmpresa() {
        cenario.enviar(Method.DELETE, EMPRESAS + "/" + cenario.empresa.id(), null);
    }

    @Então("o corpo traz os dados da empresa cadastrada")
    public void oCorpoTrazOsDadosDaEmpresaCadastrada() {
        Response resposta = cenario.resposta();
        assertThat(resposta.jsonPath().getLong("id")).isEqualTo(cenario.empresa.id());
        cenario.empresa.dados().forEach((campo, valor) ->
                assertThat(resposta.<Object>path(campo)).as(campo).isEqualTo(valor));
    }

    @Então("a empresa aparece na listagem de empresas")
    public void aEmpresaApareceNaListagem() {
        long id = cenario.resposta().jsonPath().getLong("id");
        assertThat(cenario.ler(EMPRESAS).jsonPath().getList("id", Long.class)).contains(id);
    }

    @Então("a empresa cadastrada está na lista")
    public void aEmpresaCadastradaEstaNaLista() {
        assertThat(cenario.resposta().jsonPath().getList("id", Long.class)).contains(cenario.empresa.id());
    }

    @Então("a empresa não é mais encontrada")
    public void aEmpresaNaoEMaisEncontrada() {
        cenario.conferirQueNaoExiste(EMPRESAS + "/" + cenario.empresa.id());
    }
}
