plugins {
    java
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.allopen") version "1.8.22"
    id("org.jooq.jooq-codegen-gradle") version "3.19.3"
    id("org.liquibase.gradle") version "2.2.1"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val changeLogFile: String by project

val databaseDriver: String by project
val databaseUrl: String by project
val databaseUsername: String by project
val databasePassword: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-liquibase")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("org.jooq:jooq:3.19.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.testcontainers:postgresql:1.17.2")
    implementation("org.jooq:jooq-meta-extensions-liquibase:3.19.3")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")

    liquibaseRuntime("org.liquibase:liquibase-core:4.26.0")
    liquibaseRuntime("org.postgresql:postgresql:42.7.1")
    liquibaseRuntime("info.picocli:picocli:4.6.1")

    jooqCodegen("org.postgresql:postgresql:42.7.1")
    jooqCodegen("org.jooq:jooq-meta-extensions-liquibase:3.19.3")
}

group = "ru.itis"
version = "1.0-SNAPSHOT"

sourceSets.main {
    java.srcDirs.add(file("build/generated-sources/grpc"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
}

tasks.register("generateJooq") {
    doLast {
        tasks.jooqCodegen.get().execute(null)
    }
}

tasks.named("compileKotlin") {
    dependsOn("generateJooq")
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "changeLogFile" to file(changeLogFile),
            "url" to databaseUrl,
            "username" to databaseUsername,
            "password" to databasePassword
        )
    }
}

jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"

            database {
                name = "org.jooq.meta.extensions.liquibase.LiquibaseDatabase"

                properties {
                    property {
                        key = "dialect"
                        value = "POSTGRESQL"
                    }

                    property {
                        key = "scripts"
                        value = changeLogFile
                    }

                    property {
                        key = "rootPath"
                        value = "$projectDir"
                    }
                }
            }

            target {
                packageName = "org.jooq.quizarius"
                directory = "build/generated-sources/grpc"
            }

            generate {
                isPojosAsKotlinDataClasses = true
            }
        }
    }
}
