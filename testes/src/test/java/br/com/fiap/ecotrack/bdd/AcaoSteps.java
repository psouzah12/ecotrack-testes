package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.Method;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AcaoSteps {

    private static final String ACOES = "/api/acoes-esg";

    private final Cenario cenario;

    public AcaoSteps(Cenario cenario) {
        this.cenario = cenario;
    }

    @Dado("que a empresa tem uma ação de {string} com status {string} e impacto de {bigdecimal}")
    public void queAEmpresaTemUmaAcao(String categoria, String status, BigDecimal impacto) {
        cenario.acao = cenario.criar(ACOES, Dados.acao(cenario.empresa.id(), categoria, status, impacto));
        cenario.acoesPorCategoria.put(categoria, cenario.acao);
    }

    @Quando("eu cadastro a ação:")
    public void euCadastroAAcao(Map<String, String> campos) {
        Map<String, Object> dados = new LinkedHashMap<>(campos);
        dados.put("empresaId", cenario.empresa.id());
        dados.computeIfPresent("impactoEstimado", (campo, valor) -> Dados.decimal(valor));
        cenario.enviar(Method.POST, ACOES, dados);
    }

    @Quando("eu cadastro uma ação só com a descrição")
    public void euCadastroUmaAcaoSoComADescricao() {
        cenario.enviar(Method.POST, ACOES, Map.of(
                "empresaId", cenario.empresa.id(),
                "descricao", "Ação sem os campos obrigatórios"));
    }

    @Quando("eu mudo o status da ação para {string}")
    public void euMudoOStatusDaAcao(String status) {
        mudarStatus(cenario.acao.id(), status);
    }

    @Quando("eu mudo o status da ação de id {long} para {string}")
    public void euMudoOStatusDaAcaoDeId(long id, String status) {
        mudarStatus(id, status);
    }

    @Quando("eu consulto a ação")
    public void euConsultoAAcao() {
        cenario.get(ACOES + "/" + cenario.acao.id());
    }

    @Quando("eu listo as ações da empresa")
    public void euListoAsAcoesDaEmpresa() {
        cenario.get(ACOES + "/empresa/" + cenario.empresa.id());
    }

    @Quando("eu listo as ações com status {string}")
    public void euListoAsAcoesComStatus(String status) {
        cenario.get(ACOES + "?status=" + status);
    }

    @Quando("eu altero o título da ação para {string} e o impacto para {bigdecimal}")
    public void euAlteroATituloEImpacto(String titulo, BigDecimal impacto) {
        Map<String, Object> dados = new LinkedHashMap<>(cenario.acao.dados());
        dados.put("titulo", titulo);
        dados.put("impactoEstimado", impacto);
        cenario.enviar(Method.PUT, ACOES + "/" + cenario.acao.id(), dados);
    }

    @Quando("eu excluo a ação")
    public void euExcluoAAcao() {
        cenario.enviar(Method.DELETE, ACOES + "/" + cenario.acao.id(), null);
    }

    @Então("a ação cadastrada está na lista")
    public void aAcaoCadastradaEstaNaLista() {
        assertThat(cenario.resposta().jsonPath().getList("id", Long.class)).contains(cenario.acao.id());
    }

    @Então("a ação não é mais encontrada")
    public void aAcaoNaoEMaisEncontrada() {
        cenario.conferirQueNaoExiste(ACOES + "/" + cenario.acao.id());
    }

    private void mudarStatus(long id, String status) {
        cenario.enviar(Method.PATCH, ACOES + "/" + id + "/status", Map.of("status", status));
    }
}
