package br.com.fiap.ecotrack.bdd;

import io.restassured.RestAssured;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// Estado de um cenário. O Cucumber cria um Cenario novo para cada cenário e o
// PicoContainer passa o mesmo objeto para todas as classes de steps.
public class Cenario {

    record Registro(long id, Map<String, Object> dados) {
    }

    Registro empresa;
    Registro indicador;
    Registro acao;
    final Map<String, Registro> acoesPorCategoria = new HashMap<>();

    private final StringBuilder historico = new StringBuilder();
    // o que o cenário cadastrou, para apagar no fim (o último criado sai primeiro)
    private final Deque<String> criados = new ArrayDeque<>();
    private Api.Credencial credencial;
    private Response resposta;

    void autenticarComo(Api.Credencial credencial) {
        this.credencial = credencial;
    }

    // a requisição que o cenário está testando: a resposta fica guardada para os passos "Então"
    void enviar(Method metodo, String caminho, Object corpo) {
        RequestSpecification requisicao = requisicao(credencial);
        if (corpo != null) {
            requisicao.body(corpo);
        }
        resposta = requisicao.request(metodo, caminho);
        if (metodo == Method.POST && resposta.statusCode() == 201) {
            criados.push(caminho + "/" + resposta.jsonPath().getLong("id"));
        }
    }

    void get(String caminho) {
        enviar(Method.GET, caminho, null);
    }

    Response resposta() {
        assertThat(resposta).as("o cenário ainda não fez nenhuma requisição").isNotNull();
        return resposta;
    }

    // consulta usada só para conferir o resultado, sem trocar a resposta guardada
    Response ler(String caminho) {
        return requisicao(null).get(caminho);
    }

    // cadastro de apoio para o "Dado", sempre com o ADMIN
    Registro criar(String caminho, Map<String, Object> dados) {
        Response criado = requisicao(Api.ADMIN).body(dados).post(caminho);
        assertThat(criado.statusCode())
                .as("cadastro de apoio em %s respondeu %s", caminho, criado.asString())
                .isEqualTo(201);
        long id = criado.jsonPath().getLong("id");
        criados.push(caminho + "/" + id);
        return new Registro(id, dados);
    }

    void apagarCriados() {
        while (!criados.isEmpty()) {
            RestAssured.given()
                    .baseUri(Api.URL)
                    .auth().preemptive().basic(Api.ADMIN.usuario(), Api.ADMIN.senha())
                    .delete(criados.pop());
        }
    }

    String historico() {
        return historico.toString();
    }

    private RequestSpecification requisicao(Api.Credencial credencial) {
        RequestSpecification requisicao = RestAssured.given()
                .baseUri(Api.URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(this::registrar);
        if (credencial != null) {
            requisicao.auth().preemptive().basic(credencial.usuario(), credencial.senha());
        }
        return requisicao;
    }

    // guarda cada chamada para anexar ao relatório do Cucumber
    private Response registrar(FilterableRequestSpecification requisicao,
                               FilterableResponseSpecification especificacao,
                               FilterContext contexto) {
        Response r = contexto.next(requisicao, especificacao);
        historico.append(requisicao.getMethod()).append(' ').append(requisicao.getURI()).append('\n');
        if (requisicao.getBody() != null) {
            historico.append(requisicao.<Object>getBody()).append('\n');
        }
        historico.append("=> ").append(r.getStatusLine()).append(" (").append(r.getTime()).append(" ms)\n");
        if (!r.asString().isEmpty()) {
            historico.append(r.asPrettyString()).append('\n');
        }
        historico.append('\n');
        return r;
    }
}
