package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.pt.Então;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

// passos "Então" que valem para qualquer endpoint
public class RespostaSteps {

    private final Cenario cenario;

    public RespostaSteps(Cenario cenario) {
        this.cenario = cenario;
    }

    @Então("a resposta tem status {int}")
    public void aRespostaTemStatus(int status) {
        Response resposta = cenario.resposta();
        assertThat(resposta.statusCode()).as("status (corpo: %s)", resposta.asString()).isEqualTo(status);
    }

    @Então("o corpo segue o contrato {string}")
    public void oCorpoSegueOContrato(String contrato) {
        assertThat(Contrato.violacoes(contrato, cenario.resposta().asString()))
                .as("violações do contrato %s.json", contrato)
                .isEmpty();
    }

    @Então("o campo {string} vale {string}")
    public void oCampoVale(String campo, String valor) {
        assertThat(cenario.resposta().<Object>path(campo)).as(campo).isEqualTo(valor);
    }

    @Então("o campo {string} vale {bigdecimal}")
    public void oCampoValeNumero(String campo, BigDecimal valor) {
        assertThat(Dados.decimal(cenario.resposta().path(campo))).as(campo).isEqualByComparingTo(valor);
    }

    @Então("o campo {string} vale o nome da empresa")
    public void oCampoValeONomeDaEmpresa(String campo) {
        assertThat(cenario.resposta().<Object>path(campo)).as(campo).isEqualTo(cenario.empresa.dados().get("nome"));
    }

    @Então("a mensagem de erro é {string}")
    public void aMensagemDeErroE(String mensagem) {
        assertThat(cenario.resposta().<String>path("mensagem")).isEqualTo(mensagem);
    }

    @Então("o campo {string} tem o erro {string}")
    public void oCampoTemOErro(String campo, String mensagem) {
        List<Map<String, String>> erros = cenario.resposta().jsonPath().getList("$");
        assertThat(erros)
                .extracting(erro -> erro.get("campo"), erro -> erro.get("mensagem"))
                .contains(tuple(campo, mensagem));
    }

    @Então("a lista tem {int} itens")
    public void aListaTemItens(int quantidade) {
        assertThat(cenario.resposta().jsonPath().getList("$")).hasSize(quantidade);
    }

    @Então("todos os itens têm {string} igual a {string}")
    public void todosOsItensTem(String campo, String valor) {
        List<String> valores = cenario.resposta().jsonPath().getList(campo);
        assertThat(valores).isNotEmpty().containsOnly(valor);
    }

    @Então("a resposta chega em menos de {int} ms")
    public void aRespostaChegaEmMenosDe(int limite) {
        assertThat(cenario.resposta().time()).isLessThan(limite);
    }

    @Então("a resposta pede autenticação Basic")
    public void aRespostaPedeAutenticacaoBasic() {
        assertThat(cenario.resposta().header("WWW-Authenticate")).startsWith("Basic");
    }
}
