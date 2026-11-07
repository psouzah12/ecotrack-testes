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

    // no CI e no docker compose a API pode ainda estar subindo (o Oracle leva 1 a 2 minutos)
    @BeforeAll
    public static void esperarApi() throws InterruptedException {
        Instant limite = Instant.now().plus(Duration.ofMinutes(3));
        while (!apiPronta()) {
            if (Instant.now().isAfter(limite)) {
                throw new IllegalStateException("a API não ficou pronta em " + Api.URL + "/actuator/health/readiness");
            }
            Thread.sleep(3000);
        }
    }

    private static boolean apiPronta() {
        try {
            return RestAssured.get(Api.URL + "/actuator/health/readiness").statusCode() == 200;
        } catch (Exception conexaoRecusada) {
            return false;
        }
    }

    @After
    public void depoisDoCenario(Scenario scenario) {
        // anexo como texto: em bytes o relatório HTML decodifica sem UTF-8 e estraga os acentos
        scenario.attach(cenario.historico(), "text/plain", "Requisições do cenário");
        cenario.apagarCriados();
    }
}
