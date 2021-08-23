import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.asciidoctor.convert") version "1.5.9.2"
}

dependencies {
    implementation(project(":investment-core"))
    implementation(project(":investment-user-domain"))
    implementation(project(":investment-product-domain"))
}

tasks.getByName("asciidoctor") {
    enabled = true
    inputs.dir("build/generated-snippets")
    dependsOn(setOf("test"))

    doFirst {
        delete {
            file("src/main/resources/static/docs")
        }
    }

    doLast {
        copy {
            from("build/asciidoc/html5")
            into("src/main/resources/static/docs")
        }
    }
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    setDependsOn(setOf("asciidoctor"))
    archiveClassifier.set("boot")
    manifest {
        attributes["Main-Class"] = "org.springframework.boot.loader.JarLauncher"
        attributes["Start-Class"] = "com.digimon.investment.api.InvestmentApplication"
    }
}



