package br.com.fiap.ecotrack.bdd;

// Endereço da API e usuários. Cada valor pode vir de -D na linha do Maven ou de
// variável de ambiente; sem nenhum dos dois vale o padrão da API rodando local.
final class Api {

    static final String URL = config("ECOTRACK_BASE_URL", "http://localhost:8080");

    static final Credencial ADMIN = new Credencial(
            config("ECOTRACK_ADMIN_USER", "admin@ecotrack.com"),
            config("ECOTRACK_ADMIN_PASSWORD", "admin123"));

    static final Credencial USER = new Credencial(
            config("ECOTRACK_USER", "user@ecotrack.com"),
            config("ECOTRACK_USER_PASSWORD", "user123"));

    private Api() {
    }

    private static String config(String nome, String padrao) {
        String valor = System.getProperty(nome, System.getenv(nome));
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    record Credencial(String usuario, String senha) {

        Credencial comSenha(String outraSenha) {
            return new Credencial(usuario, outraSenha);
        }
    }
}
