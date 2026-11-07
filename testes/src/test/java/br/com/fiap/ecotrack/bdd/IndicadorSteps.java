package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.http.Method;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class IndicadorSteps {

    private static final String INDICADORES = "/api/indicadores";
    private static final String MES_PADRAO = "2026-10-01";

    private final Cenario cenario;

    public IndicadorSteps(Cenario cenario) {
        this.cenario = cenario;
    }

    @Dado("que a empresa registrou {bigdecimal} {string} de {string}")
    public void queAEmpresaRegistrou(BigDecimal valor, String unidade, String tipo) {
        cenario.indicador = cenario.criar(INDICADORES,
                Dados.indicador(cenario.empresa.id(), tipo, valor, unidade, MES_PADRAO));
    }

    @Quando("eu registro {bigdecimal} {string} de {string} referente a {string}")
    public void euRegistro(BigDecimal valor, String unidade, String tipo, String data) {
        cenario.enviar(Method.POST, INDICADORES, Dados.indicador(cenario.empresa.id(), tipo, valor, unidade, data));
    }

    @Quando("eu registro um indicador para a empresa de id {long}")
    public void euRegistroUmIndicadorParaAEmpresaDeId(long empresaId) {
        cenario.enviar(Method.POST, INDICADORES,
                Dados.indicador(empresaId, "AGUA", new BigDecimal("100"), "m3", MES_PADRAO));
    }

    @Quando("eu consulto o indicador")
    public void euConsultoOIndicador() {
        cenario.get(INDICADORES + "/" + cenario.indicador.id());
    }

    @Quando("eu listo os indicadores da empresa")
    public void euListoOsIndicadoresDaEmpresa() {
        cenario.get(INDICADORES + "/empresa/" + cenario.empresa.id());
    }

    @Quando("eu listo os indicadores do tipo {string}")
    public void euListoOsIndicadoresDoTipo(String tipo) {
        cenario.get(INDICADORES + "?tipo=" + tipo);
    }

    @Quando("eu corrijo o valor do indicador para {bigdecimal}")
    public void euCorrijoOValorDoIndicador(BigDecimal valor) {
        Map<String, Object> dados = new LinkedHashMap<>(cenario.indicador.dados());
        dados.put("valor", valor);
        cenario.enviar(Method.PUT, INDICADORES + "/" + cenario.indicador.id(), dados);
    }

    @Quando("eu excluo o indicador")
    public void euExcluoOIndicador() {
        cenario.enviar(Method.DELETE, INDICADORES + "/" + cenario.indicador.id(), null);
    }

    @Então("o indicador registrado está na lista")
    public void oIndicadorRegistradoEstaNaLista() {
        assertThat(cenario.resposta().jsonPath().getList("id", Long.class)).contains(cenario.indicador.id());
    }

    @Então("o indicador não é mais encontrado")
    public void oIndicadorNaoEMaisEncontrado() {
        cenario.conferirQueNaoExiste(INDICADORES + "/" + cenario.indicador.id());
    }
}
