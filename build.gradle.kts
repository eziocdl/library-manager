plugins {
    id("java")
}

group = "com.managerlibrary"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.postgresql:postgresql:42.7.2")
    implementation("org.postgresql:postgresql")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}