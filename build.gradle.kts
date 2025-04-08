plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13" // ou a versão mais recente
}

group = "com.managerlibrary"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.postgresql:postgresql:42.7.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}



tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.managerlibrary.Main"
    }

}
