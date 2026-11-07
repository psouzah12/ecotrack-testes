package br.com.fiap.ecotrack.bdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.util.List;

// Contratos das respostas em src/test/resources/schemas (JSON Schema 2020-12).
// O "$ref" entre arquivos é resolvido pelo classpath.
final class Contrato {

    private static final JsonSchemaFactory FABRICA = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
    // no draft 2020-12 o "format" é só anotação; ligado, "2026-13-45" deixa de passar como data.
    // JSON_PATH deixa o campo da violação no formato "$.dataCadastro".
    private static final SchemaValidatorsConfig CONFIG = SchemaValidatorsConfig.builder()
            .formatAssertionsEnabled(true)
            .pathType(PathType.JSON_PATH)
            .build();
    private static final ObjectMapper JSON = new ObjectMapper();

    private Contrato() {
    }

    static List<String> violacoes(String contrato, String corpo) {
        JsonSchema schema = FABRICA.getSchema(SchemaLocation.of("classpath:schemas/" + contrato + ".json"), CONFIG);
        try {
            return schema.validate(JSON.readTree(corpo)).stream()
                    .map(ValidationMessage::getMessage)
                    .sorted()
                    .toList();
        } catch (JsonProcessingException e) {
            return List.of("a resposta não é JSON: " + corpo);
        }
    }
}
