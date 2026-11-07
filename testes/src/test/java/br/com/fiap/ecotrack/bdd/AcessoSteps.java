package br.com.fiap.ecotrack.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.restassured.http.Method;

import java.util.Map;

public class AcessoSteps {

    private final Cenario cenario;

    public AcessoSteps(Cenario cenario) {
        this.cenario = cenario;
    }

    @Dado("que estou autenticado como {word}")
    public void queEstouAutenticadoComo(String perfil) {
        cenario.autenticarComo(switch (perfil) {
            case "ADMIN" -> Api.ADMIN;
            case "USER" -> Api.USER;
            default -> throw new IllegalArgumentException("perfil desconhecido: " + perfil);
        });
    }

    @Dado("que estou autenticado como ADMIN com a senha {string}")
    public void queEstouAutenticadoComoAdminComASenha(String senha) {
        cenario.autenticarComo(Api.ADMIN.comSenha(senha));
    }

    @Dado("que não estou autenticado")
    public void queNaoEstouAutenticado() {
        cenario.autenticarComo(null);
    }

    @Quando("eu consulto {string}")
    public void euConsulto(String caminho) {
        cenario.get(caminho);
    }

    // a permissão é checada antes do corpo ser lido, então um corpo vazio basta
    @Quando("eu envio {word} para {string}")
    public void euEnvioPara(String metodo, String caminho) {
        cenario.enviar(Method.valueOf(metodo), caminho, Map.of());
    }

    @Quando("eu tento cadastrar uma empresa")
    public void euTentoCadastrarUmaEmpresa() {
        cenario.enviar(Method.POST, "/api/empresas", Dados.empresa("Cadastro sem permissão"));
    }
}
