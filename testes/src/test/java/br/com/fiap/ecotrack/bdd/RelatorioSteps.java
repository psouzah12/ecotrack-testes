package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.path.json.JsonPath;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class RelatorioSteps {

    private static final String RELATORIOS = "/api/relatorios";

    private final Cenario cenario;

    public RelatorioSteps(Cenario cenario) {
        this.cenario = cenario;
    }

    @Quando("eu consulto o resumo da empresa")
    public void euConsultoOResumoDaEmpresa() {
        cenario.get(RELATORIOS + "/empresa/" + cenario.empresa.id());
    }

    @Quando("eu consulto o resumo da empresa de id {long}")
    public void euConsultoOResumoDaEmpresaDeId(long id) {
        cenario.get(RELATORIOS + "/empresa/" + id);
    }

    @Quando("eu consulto o resumo geral")
    public void euConsultoOResumoGeral() {
        cenario.get(RELATORIOS + "/resumo");
    }

    @Quando("eu consulto o impacto da ação de {string}")
    public void euConsultoOImpactoDaAcaoDe(String categoria) {
        cenario.get(RELATORIOS + "/impacto/" + cenario.acoesPorCategoria.get(categoria).id());
    }

    @Quando("eu consulto o impacto da ação de id {long}")
    public void euConsultoOImpactoDaAcaoDeId(long id) {
        cenario.get(RELATORIOS + "/impacto/" + id);
    }

    @Então("o resumo mostra:")
    public void oResumoMostra(Map<String, BigDecimal> esperado) {
        JsonPath resumo = cenario.resposta().jsonPath();
        assertSoftly(s -> esperado.forEach((campo, valor) ->
                s.assertThat(Dados.decimal(resumo.get(campo))).as(campo).isEqualByComparingTo(valor)));
    }

    // governança: os totais do relatório precisam bater com o que as listagens devolvem
    @Então("os totais do resumo geral batem com as listagens de empresas, indicadores e ações")
    public void osTotaisBatemComAsListagens() {
        JsonPath resumo = cenario.resposta().jsonPath();
        int empresas = cenario.ler("/api/empresas").jsonPath().getList("$").size();
        int indicadores = cenario.ler("/api/indicadores").jsonPath().getList("$").size();
        List<Object> impactos = cenario.ler("/api/acoes-esg").jsonPath().getList("impactoEstimado");
        int concluidas = cenario.ler("/api/acoes-esg?status=CONCLUIDA").jsonPath().getList("$").size();
        BigDecimal impactoTotal = impactos.stream()
                .filter(Objects::nonNull)
                .map(Dados::decimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertSoftly(s -> {
            s.assertThat(resumo.getInt("totalEmpresas")).as("totalEmpresas").isEqualTo(empresas);
            s.assertThat(resumo.getInt("totalIndicadores")).as("totalIndicadores").isEqualTo(indicadores);
            s.assertThat(resumo.getInt("totalAcoes")).as("totalAcoes").isEqualTo(impactos.size());
            s.assertThat(resumo.getInt("acoesConcluidas")).as("acoesConcluidas").isEqualTo(concluidas);
            s.assertThat(Dados.decimal(resumo.get("impactoTotalEstimado")))
                    .as("impactoTotalEstimado").isEqualByComparingTo(impactoTotal);
        });
    }
}
