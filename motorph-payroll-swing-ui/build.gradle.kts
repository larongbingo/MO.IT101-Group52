plugins {
    java
    application
    id("io.github.file5.guidesigner") version "1.0.2"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("resources"))
        }
    }
    test {
        java {
            setSrcDirs(listOf("test"))
        }
    }
}

dependencies {
    implementation(project(":motorph-payroll-core"))
    implementation("de.siegmar:fastcsv:4.1.1")
    implementation("com.google.inject:guice:7.0.0")
    
    // IntelliJ GUI Designer Runtime
    implementation("com.intellij:forms_rt:7.0.3")

    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.motorph.Main")
}

tasks.test {
    useJUnitPlatform()
}
