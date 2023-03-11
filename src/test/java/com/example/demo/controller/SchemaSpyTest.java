package com.example.demo.controller;


import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

class SchemaSpyTest extends AbstractControllerTest {

  @Test
  @SneakyThrows
  void schemaSpy() {
    @Cleanup final var schemaSpy =
        new GenericContainer<>(DockerImageName.parse("schemaspy/schemaspy:6.1.0"))
            .withNetworkAliases("schemaspy")
            .withNetwork(NETWORK)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("SchemaSpy")))
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint(""))
            .withCommand("sleep 500000");

    schemaSpy.start();
    schemaSpy.execInContainer(
        "java",
        "-jar", "/schemaspy-6.1.0.jar",
        "-t", "pgsql11",
        "-db", POSTGRES.getDatabaseName(),
        "-host", "postgres",
        "-u", POSTGRES.getUsername(),
        "-p", POSTGRES.getPassword(),
        "-o", "/output",
        "-dp", "/drivers_inc",
        "-debug"
    );
    schemaSpy.execInContainer("tar", "-czvf", "/output/output.tar.gz", "/output");
    final var buildFolderPath =
        Path.of(getClass().getResource("/").toURI()).toAbsolutePath();
    schemaSpy.copyFileFromContainer(
        "/output/output.tar.gz",
        buildFolderPath.resolve("output.tar.gz").toString()
    );
    schemaSpy.stop();

    final var archiver = ArchiverFactory.createArchiver("tar", "gz");
    archiver.extract(buildFolderPath.resolve("output.tar.gz").toFile(),
        buildFolderPath.toFile());

    Files.writeString(
        buildFolderPath.resolve("index.html"),
        """
            <html>
            <head>
                <meta charset="UTF8">
                <style>
                    body, table {
                        font-family: "JetBrains Mono";
                        font-size: 20px;
                    }
                    table, th, td {
                      border: 1px solid black;
                    }
                </style>
                <link href='https://fonts.googleapis.com/css?family=JetBrains Mono' rel='stylesheet'>
            </head>
            <body>
                <div>
                    <h3><a href="security-docs">Endpoints role checking</a></h3>
                    <h3><a href="output">Schema Spy</a></h3>
                </div>
            </body>
            </html>"""
    );
  }
}
