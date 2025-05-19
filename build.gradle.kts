plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}



        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
                vendor.set(JvmVendorSpec.ORACLE)
            }
        }

group = "com.managerlibrary"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.openjfx:javafx-base:21")

    implementation("org.postgresql:postgresql:42.7.2")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.base")
}

tasks.jar { // Acesse a tarefa 'jar' diretamente dentro de 'tasks'

    from(sourceSets.main.get().resources) {
        into("")
    }
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

application {
    mainClass.set("com.managerlibrary.Main")
    applicationDefaultJvmArgs = listOf(
            "-Djavafx.platform.explicit=true",
            "-Djavafx.verbose=true",
            "-Dprism.order=sw", // Ou "hw", tente ambos
            "-Dprism.verbose=true"
    )
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}