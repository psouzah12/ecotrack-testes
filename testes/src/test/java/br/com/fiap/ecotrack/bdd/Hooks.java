package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;

import java.time.Duration;
import java.time.Instant;

public class Hooks {

    private final Cenario cenario;

    public Hooks(Cenario cenario) {
        this.cenario = cenario;
    }

    // O script e o workflow já esperam a API ficar healthy (docker compose up --wait).
    // Esta espera cobre quem roda mvn test logo depois de subir o ambiente sem o --wait.
    @BeforeAll
    public static void esperarApi() throws InterruptedException {
        Instant limite = Instant.now().plus(Duration.ofMinutes(2));
        String ultimoResultado = "";
        while (Instant.now().isBefore(limite)) {
            try {
                int status = RestAssured.get(Api.URL + "/actuator/health/readiness").statusCode();
                if (status == 200) {
                    return;
                }
                ultimoResultado = "HTTP " + status;
            } catch (Exception e) {
                // enquanto o container sobe a conexão é recusada; a causa vai na mensagem final
                ultimoResultado = e.toString();
            }
            Thread.sleep(3000);
        }
        throw new IllegalStateException("a API não ficou pronta em " + Api.URL
                + "/actuator/health/readiness (último resultado: " + ultimoResultado + ")");
    }

    @After
    public void depoisDoCenario(Scenario scenario) {
        // anexo como texto: em bytes o relatório HTML decodifica sem UTF-8 e estraga os acentos
        scenario.attach(cenario.historico(), "text/plain", "Requisições do cenário");
        cenario.apagarCriados();
    }
}
