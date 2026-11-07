package br.com.fiap.ecotrack.bdd;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

// Corpos das requisições. Cada chamada devolve um mapa novo, que o step pode alterar.
final class Dados {

    private static final Map<String, String> TITULOS = Map.of(
            "RECICLAGEM", "Coleta seletiva no refeitório",
            "EFICIENCIA_ENERGETICA", "Troca das lâmpadas do galpão por LED",
            "REDUCAO_CONSUMO", "Reúso da água da lavagem de pisos",
            "COMPENSACAO_CARBONO", "Plantio de mudas nativas",
            "EDUCACAO_AMBIENTAL", "Treinamento sobre descarte de resíduos");

    private Dados() {
    }

    static Map<String, Object> empresa(String nome) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("nome", nome);
        dados.put("cnpj", cnpjNovo());
        dados.put("setor", "Reciclagem");
        dados.put("cidade", "Santos");
        dados.put("estado", "SP");
        return dados;
    }

    static Map<String, Object> indicador(long empresaId, String tipo, BigDecimal valor, String unidade, String data) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("empresaId", empresaId);
        dados.put("tipo", tipo);
        dados.put("valor", valor);
        dados.put("unidadeMedida", unidade);
        dados.put("dataReferencia", data);
        dados.put("observacao", "Lançado pelos testes BDD");
        return dados;
    }

    static Map<String, Object> acao(long empresaId, String categoria, String status, BigDecimal impacto) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("empresaId", empresaId);
        dados.put("titulo", TITULOS.getOrDefault(categoria, "Ação de " + categoria));
        dados.put("descricao", "Cadastrada pelos testes BDD");
        dados.put("categoria", categoria);
        dados.put("status", status);
        dados.put("impactoEstimado", impacto);
        dados.put("dataInicio", "2026-11-16");
        dados.put("dataFimPrevista", "2027-02-16");
        return dados;
    }

    // número vindo do JSON ou de uma tabela do Gherkin escrita com vírgula ("350,00")
    static BigDecimal decimal(Object valor) {
        return new BigDecimal(String.valueOf(valor).replace(',', '.'));
    }

    // a API confere só o formato do CNPJ; o prefixo 98 identifica dado criado pelos testes
    private static String cnpjNovo() {
        String d = "%012d".formatted(ThreadLocalRandom.current().nextLong(1_000_000_000_000L));
        return "98.%s.%s/%s-%s".formatted(d.substring(0, 3), d.substring(3, 6), d.substring(6, 10), d.substring(10));
    }
}
