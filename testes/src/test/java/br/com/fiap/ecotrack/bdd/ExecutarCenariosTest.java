package br.com.fiap.ecotrack.bdd;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

// roda todos os .feature de src/test/resources/features; a configuração do Cucumber
// fica em src/test/resources/junit-platform.properties
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class ExecutarCenariosTest {
}
