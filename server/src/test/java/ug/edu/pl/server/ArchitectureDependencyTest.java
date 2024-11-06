package ug.edu.pl.server;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureDependencyTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("ug.edu.pl.server");

    @Test
    void checkDependencies() {
        layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("infrastructure").definedBy("ug.edu.pl.server.infrastructure..")
                .layer("domain").definedBy("ug.edu.pl.server.domain..")
                .whereLayer("infrastructure").mayOnlyAccessLayers("domain")
                .whereLayer("domain").mayNotAccessAnyLayer()
                .check(classes);
    }
}
